package sk.alival.crutch.pager

import java.net.ConnectException
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import sk.alival.crutch.coroutines.launchIO
import sk.alival.crutch.kover.KoverIgnore
import sk.alival.crutch.logging.getNameForLogs
import kotlin.math.ceil
import kotlin.math.min

/**
 * Custom paging implementation
 *
 * @param ITEM_TYPE type of items we are operating with
 */
abstract class Pager<ITEM_TYPE : PagerItemType> {

    companion object {
        const val defaultPagerPageSize = 25
    }

    /**
     * Page size for the pager.
     * Defaults to 25.
     */
    open val pageSize: AtomicInteger = AtomicInteger(defaultPagerPageSize)

    /**
     * Item offset before next page, if set to 0, only last item will trigger nextPage.
     * Defaults to 0.
     */
    open val itemOffsetBeforeNextPage: AtomicInteger = AtomicInteger(0)

    @Volatile
    private var actualPage: AtomicInteger = AtomicInteger(1)

    @Volatile
    private var totalPages: AtomicInteger? = null

    @Volatile
    private var totalItemsCount: AtomicInteger = AtomicInteger(1)

    @Volatile
    private var pageFetchingJob: Job? = null

    private var pagedItems: MutableMap<Int, PagingItemsData<ITEM_TYPE>> = mutableMapOf()

    private var pagingStatesFlow: MutableStateFlow<PagerStates<ITEM_TYPE>?> = MutableStateFlow(null)

    /**
     * Get page
     *
     * @param pageNumber number of page the implementer should fetch, starts with 1
     * @return data for the page
     */
    abstract suspend fun getPage(pageNumber: Int): PagingItemsData<ITEM_TYPE>

    /**
     * [Flow] to listen for [PagerStates]
     *
     */
    fun listenForPagingStates(): Flow<PagerStates<ITEM_TYPE>> {
        return pagingStatesFlow.filterNotNull()
            .onEach {
                log("state: $it  type: ${it::class.java.getNameForLogs()}")
            }
    }

    /**
     * On item rendered
     *
     * @param index  index of item that have been rendered
     * @param scope  coroutine scope
     * @param isNetworkAvailable  network state
     */
    @KoverIgnore
    fun onItemRendered(index: Int, scope: CoroutineScope, isNetworkAvailable: Boolean? = null) {
        val numberFromIndex = index + 1 + itemOffsetBeforeNextPage.get() // converting index to number, adding offset
        val nextItemNumber = numberFromIndex + 1 // number of next item
        val nextPageNumber = getPageFromItemNumber(nextItemNumber) // number of page where nextItemNumber belongs
        val isNextPageAvailable = totalPages?.get()?.let { nextPageNumber <= it } ?: true // determine if the next page is available
        val isNextPageAlreadyDownloaded = pagedItems.containsKey(nextPageNumber)
        val isAllowedToDownloadNextPage = !isNextPageAlreadyDownloaded && isNextPageAvailable
        log(
            """
            numberFromIndex: $numberFromIndex
            nextItemNumber: $nextItemNumber
            nextPageNumber: $nextPageNumber
            isNextPageAvailable: $isNextPageAvailable
            isNextPageAlreadyDownloaded: $isNextPageAlreadyDownloaded
            isAllowedToDownloadNextPage: $isAllowedToDownloadNextPage
            """.trimIndent()
        )
        getNextPage(
            pagerFlag = PagerFlags.Paging,
            nextPageNumber = nextPageNumber,
            scope = scope,
            resetBeforeSuccess = false,
            isAllowedToDownloadNextPage = isAllowedToDownloadNextPage,
            isNetworkAvailable = isNetworkAvailable
        )
    }

    /**
     * Get first page
     *
     * @param scope  on which we operate the paging
     * @param isNetworkAvailable  check if network is available
     * @param resetBeforeFirstPage  flag to mark if we should completely reset the pager
     */
    suspend fun getFirstPage(
        scope: CoroutineScope,
        isNetworkAvailable: Boolean? = null,
        resetBeforeFirstPage: Boolean = true,
    ) {
        if (resetBeforeFirstPage) {
            reset()
        }
        getNextPage(
            pagerFlag = PagerFlags.Initial,
            nextPageNumber = 1,
            scope = scope,
            resetBeforeSuccess = false,
            isNetworkAvailable = isNetworkAvailable
        )
    }

    /**
     * On swipe to refresh
     *
     * Automatically passing resetBeforeSuccess as true
     *
     * @param scope  on which we operate the paging
     * @param isNetworkAvailable  check if network is available
     */
    fun onSwipeToRefresh(
        scope: CoroutineScope,
        isNetworkAvailable: Boolean? = null,
    ) {
        getNextPage(
            pagerFlag = PagerFlags.SwipeToRefresh,
            nextPageNumber = 1,
            scope = scope,
            resetBeforeSuccess = true,
            isNetworkAvailable = isNetworkAvailable
        )
    }

    /**
     * Get next page
     *
     * @param pagerFlag helper flag we pass to nextPage and will be returned in the result
     * @param nextPageNumber number of page we should fetch
     * @param scope on which we operate the paging
     * @param resetBeforeSuccess if true, it will reset the counters and items before emitting items after success api call, should be used for swipeToRefresh where we want to fetch page1 again, if success clear the previous, if failed keep the existing data. Check [clearData]
     * @param isNetworkAvailable boolean to skip [getNextPage] and directly emit error
     * @param isAllowedToDownloadNextPage flag to check if we are able to download next page
     */
    private fun getNextPage(
        pagerFlag: PagerFlags,
        nextPageNumber: Int,
        scope: CoroutineScope,
        resetBeforeSuccess: Boolean,
        isNetworkAvailable: Boolean? = null,
        isAllowedToDownloadNextPage: Boolean = true
    ) {
        log("Calling nextPage")
        if (pageFetchingJob?.isCompleted != false) {
            pageFetchingJob = scope.launchIO {
                if (!isAllowedToDownloadNextPage) {
                    log("Not allowed to download more pages")
                    pagingStatesFlow.emit(PagerStates.NoMorePagesAvailable(pagedItems.toMap()))
                } else {
                    if (isNetworkAvailable != false) {
                        pagingStatesFlow.emit(PagerStates.Loading(nextPageNumber, pagerFlag, pagedItems.toMap()))
                        try {
                            val nextPageData = getPage(nextPageNumber)
                            log("nextPageData before resetBeforeSuccess: $resetBeforeSuccess, data: $nextPageData, source: $pagerFlag")
                            if (resetBeforeSuccess) {
                                clearData()
                            }
                            totalPages = AtomicInteger(getPageFromItemNumber(nextPageData.totalItemsNumber))
                            actualPage = AtomicInteger(nextPageNumber)
                            totalItemsCount = AtomicInteger(nextPageData.totalItemsNumber)
                            pagedItems[actualPage.get()] = nextPageData
                            pagingStatesFlow.emit(PagerStates.Success(pagerFlag, pagedItems.toMap()))
                            log("nextPageData after resetBeforeSuccess:$resetBeforeSuccess : $nextPageData, source: $pagerFlag")
                        } catch (t: Throwable) {
                            log(t.toString())
                            pagingStatesFlow.emit(PagerStates.Error(t, pagerFlag, pagedItems.toMap()))
                        }
                    } else {
                        log("Seems like we don't have connection")
                        pagingStatesFlow.emit(PagerStates.Error(ConnectException("No internet"), pagerFlag, pagedItems.toMap()))
                    }
                }
            }
        } else {
            log("Previous nextPage is not completed")
        }
    }

    /**
     * Get page number from item number
     *
     * @param itemNumber - number of item in the list
     * @return the number of page where the item belongs
     */
    internal fun getPageFromItemNumber(itemNumber: Int): Int {
        val itemPage = if (itemNumber <= 0) {
            1
        } else {
            ceil((itemNumber.toFloat() / pageSize.toFloat())).toInt()
        }
        return totalPages?.get()?.let {
            min(itemPage, it)
        } ?: itemPage
    }

    /**
     * Set custom total pages for testing
     *
     * @param totalPageNumber
     */
    internal fun setCustomTotalPages(totalPageNumber: Int) {
        this.totalPages = AtomicInteger(totalPageNumber)
    }

    /**
     * Get next page job for testing
     *
     */
    internal fun getNextPageJob() = pageFetchingJob

    /**
     * Refresh all pages, fetches every page we already have by its pageNumber.
     * Typical usage could be removing some items, or if the list changes we can just re-fetch the existing data
     */
    suspend fun refreshAllPages() {
        log("refreshing all pages")
        pagedItems.keys.forEach { availablePageCount ->
            getPage(availablePageCount).also {
                log("refreshing page $availablePageCount, totalItems: $totalItemsCount, itemsForPage: $it, totalItemsData: ${pagedItems.toMap()}")
            }
        }
        log("Emitting after refresh")
        pagingStatesFlow.emit(PagerStates.Success(PagerFlags.RefreshingAllPages, pagedItems.toMap()))
    }

    /**
     * Cleans pager, useful when we want to re-init pager with different configuration, also cleaning the stream for [listenForPagingStates]
     *
     */
    suspend fun cleanAll() {
        this.pagingStatesFlow = MutableStateFlow(null)
        reset()
    }

    /**
     * Reset the pager and cancel the current running [getPage]
     *
     */
    private suspend fun reset() {
        log("resetting pagerManager")
        pageFetchingJob?.cancelAndJoin()
        pageFetchingJob = null
        clearData()
    }

    /**
     * Clear data, resetting values to defaults
     *
     */
    private fun clearData() {
        actualPage = AtomicInteger(1)
        totalPages = null
        totalItemsCount = AtomicInteger(1)
        pagedItems.clear()
        log("clearing data of pagerManager")
    }

    /**
     * Is any page loaded
     *
     * @return if we loaded a page
     */
    fun isAnyPageLoaded(): Boolean {
        return totalPages != null && totalPages?.get() != 0
    }

    /**
     * Helper logger method to always log all the information
     *
     * @param message
     */
    private fun log(message: String) {
        PagerLogger.log {
            """
           $message
           actualPage: $actualPage
           totalPages: $totalPages
           totalItemsCount: $totalItemsCount
           pageSize: $pageSize
           itemOffsetBeforeNextPage: $itemOffsetBeforeNextPage
                """.trimIndent()
        }
    }

    /**
     * Paging items data, holder for data we fetch in [getPage]
     *
     * @param ITEM_TYPE type of data
     * @property totalItemsNumber got from api
     * @property items that were fetched
     */
    data class PagingItemsData<ITEM_TYPE : PagerItemType>(val totalItemsNumber: Int, val items: List<ITEM_TYPE>)
}

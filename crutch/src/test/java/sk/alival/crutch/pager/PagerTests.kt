package sk.alival.crutch.pager

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sk.alival.crutch.cacheable.CacheableDataLogger
import sk.alival.crutch.logging.CustomLogs
import sk.alival.crutch.logging.Logs

class PagerTests {

    data class PagerTestItem(
        val index: Int
    ) : PagerItemType {
        override val id: String
            get() = "$index"
    }

    inner class TestingPager : Pager<PagerTestItem>() {
        override val pageSize: AtomicInteger = AtomicInteger(2)
        override val isDebuggingEnabled: AtomicBoolean = AtomicBoolean(true)
        override suspend fun getPage(pageNumber: Int): PagingItemsData<PagerTestItem> {
            return PagingItemsData(9, fetchDataFromApi(pageNumber))
        }
    }

    private val testingPager = TestingPager()

    // total Items 9
    // total pages 5
    // items per page 2, 1 item on last page
    private fun fetchDataFromApi(pageIndex: Int): List<PagerTestItem> {
        return when (pageIndex) {
            1 -> {
                listOf(
                    PagerTestItem(0),
                    PagerTestItem(1)
                )
            }

            2 -> {
                listOf(
                    PagerTestItem(2),
                    PagerTestItem(3)
                )
            }

            3 -> {
                listOf(
                    PagerTestItem(4),
                    PagerTestItem(5)
                )
            }

            4 -> {
                listOf(
                    PagerTestItem(6),
                    PagerTestItem(7)
                )
            }

            5 -> {
                listOf(
                    PagerTestItem(8)
                )
            }

            else -> {
                emptyList()
            }
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            mockkStatic(Log::class)
            every { Log.println(any(), any(), any()) } returns 0
            Logs.init(true, customLogs = object : CustomLogs {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, tag, message, t)
                    println("$tag: $message $t")
                }
            })
            CacheableDataLogger.setCacheableDataLoggerEnabled(true)
        }
    }

    @BeforeEach
    fun beforeEach() {
        testingPager.cleanAll()
    }

    @Test
    @DisplayName("Testing page number calculation from item number")
    fun testPageFromItemCount() = runTest {
        assertEquals(1, testingPager.getPageFromItemNumber(0).also { println(it) })
        assertEquals(1, testingPager.getPageFromItemNumber(1).also { println(it) })
        assertEquals(1, testingPager.getPageFromItemNumber(2).also { println(it) })
        assertEquals(2, testingPager.getPageFromItemNumber(3).also { println(it) })
        assertEquals(2, testingPager.getPageFromItemNumber(4).also { println(it) })
        assertEquals(3, testingPager.getPageFromItemNumber(5).also { println(it) })
        assertEquals(3, testingPager.getPageFromItemNumber(6).also { println(it) })
        assertEquals(4, testingPager.getPageFromItemNumber(7).also { println(it) })
        assertEquals(4, testingPager.getPageFromItemNumber(8).also { println(it) })
        assertEquals(5, testingPager.getPageFromItemNumber(9).also { println(it) })
        // if we dont have totalPages
        assertEquals(5, testingPager.getPageFromItemNumber(10))
        // if we have totalPages
        testingPager.setCustomTotalPages(5)
        assertEquals(5, testingPager.getPageFromItemNumber(10))
    }

    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("Testing SwipeToRefresh")
    fun testSwipeToRefresh() = runTest {
        val results = testPagingStates(this) {
            testingPager.onSwipeToRefresh(this, true)
        }

        assertEquals(2, results.size)
        assert(results.filterIsInstance<PagerStates.Loading<PagerTestItem>>().isNotEmpty())

        val items = results.filterIsInstance<PagerStates.Success<PagerTestItem>>().firstOrNull()
        assert(items != null)
        assert(items?.pagerFlag == PagerFlags.SwipeToRefresh)
        assert(items?.currentItems?.flattenToItemList() == fetchDataFromApi(1))
    }

    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("Testing Initial fetch")
    fun testInitialFetch() = runTest {
        val results = testPagingStates(this) {
            testingPager.getFirstPage(scope = this, resetBeforeFirstPage = false, isNetworkAvailable = true)
        }

        assert(results.filterIsInstance<PagerStates.Loading<PagerTestItem>>().isNotEmpty())

        val items = results.filterIsInstance<PagerStates.Success<PagerTestItem>>().firstOrNull()
        assert(items != null)
        assert(items?.pagerFlag == PagerFlags.Initial)
        assert(items?.currentItems?.flattenToItemList() == fetchDataFromApi(1))
    }

    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("Testing paging")
    fun testPaging() = runTest {
        val resultsPage1 = testPagingStates(this) {
            testingPager.getFirstPage(scope = this, isNetworkAvailable = true)
        }
        assertEquals(1, resultsPage1.filterIsInstance<PagerStates.Loading<PagerTestItem>>().size)

        // ==========================================================================================
        val resultsPage2 = testPagingStates(this) {
            testingPager.onItemRendered(index = 1, scope = this, isNetworkAvailable = true)
        }
        val items2 = resultsPage2.filterIsInstance<PagerStates.Success<PagerTestItem>>().lastOrNull()
        assert(items2 != null)
        assert(items2?.pagerFlag == PagerFlags.Paging)
        assertEquals(fetchDataFromApi(1).plus(fetchDataFromApi(2)), items2?.currentItems?.flattenToItemList())

        // ==========================================================================================
        val resultsPage3 = testPagingStates(this) {
            testingPager.onItemRendered(index = 3, scope = this, isNetworkAvailable = true)
        }
        val items3 = resultsPage3.filterIsInstance<PagerStates.Success<PagerTestItem>>().lastOrNull()
        assert(items3 != null)
        assert(items3?.pagerFlag == PagerFlags.Paging)
        assertEquals(
            fetchDataFromApi(1).plus(fetchDataFromApi(2)).plus(fetchDataFromApi(3)), items3?.currentItems?.flattenToItemList()
        )

        // ==========================================================================================
        val resultsPage4 = testPagingStates(this) {
            testingPager.onItemRendered(index = 5, scope = this, isNetworkAvailable = true)
        }
        val items4 = resultsPage4.filterIsInstance<PagerStates.Success<PagerTestItem>>().lastOrNull()
        assert(items4 != null)
        assert(items4?.pagerFlag == PagerFlags.Paging)
        assertEquals(
            fetchDataFromApi(1)
                .plus(fetchDataFromApi(2))
                .plus(fetchDataFromApi(3))
                .plus(fetchDataFromApi(4)),
            items4?.currentItems?.flattenToItemList()
        )

        // ==========================================================================================
        val resultsPage5 = testPagingStates(this) {
            testingPager.onItemRendered(index = 7, scope = this, isNetworkAvailable = true)
        }
        val items5 = resultsPage5.filterIsInstance<PagerStates.Success<PagerTestItem>>().lastOrNull()
        assert(items5 != null)
        assert(items5?.pagerFlag == PagerFlags.Paging)
        assertEquals(
            fetchDataFromApi(1)
                .plus(fetchDataFromApi(2))
                .plus(fetchDataFromApi(3))
                .plus(fetchDataFromApi(4))
                .plus(fetchDataFromApi(5)),
            items5?.currentItems?.flattenToItemList()
        )

        // ==========================================================================================
        val resultsPage5LastItem = testPagingStates(this) {
            testingPager.onItemRendered(index = 8, scope = this, isNetworkAvailable = true)
        }
        val items5LastItem = resultsPage5LastItem.filterIsInstance<PagerStates.NoMorePagesAvailable<PagerTestItem>>().lastOrNull()
        assert(items5LastItem != null)

        // ==========================================================================================
        val resultsPageNotExistingItem = testPagingStates(this) {
            testingPager.onItemRendered(index = 9, scope = this, isNetworkAvailable = true)
        }
        val items5NotExistingItem = resultsPageNotExistingItem.filterIsInstance<PagerStates.NoMorePagesAvailable<PagerTestItem>>().lastOrNull()
        assert(items5NotExistingItem != null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun TestScope.testPagingStates(scope: CoroutineScope, testingBlock: suspend () -> Unit): List<PagerStates<PagerTestItem>> {
        val results = mutableListOf<PagerStates<PagerTestItem>>()
        val flow = testingPager.listenForPagingStates()
            .onEach {
                results.add(it)
            }.launchIn(scope)
        testingBlock()
        testingPager.getNextPageJob()?.join()
        advanceUntilIdle()
        flow.cancelAndJoin()
        println("\n\n======= Testing results: ========\n")
        print(results.joinToString(separator = "\n"))
        return results
    }
}

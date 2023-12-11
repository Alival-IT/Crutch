package sk.alival.crutch.pager

/**
 * Pager states
 *
 * @param T - type of item
 * @property currentItems - map of current items that will be emitted. Map<PageNumber, Items>
 */
sealed class PagerStates<T : PagerItemType>(open val currentItems: Map<Int, Pager.PagingItemsData<T>>) {
    /**
     * Loading a page
     * when [pageNumber] is 1 it means we are loading the first page usually it means screen init,
     * else we are fetching next pages
     *
     * @property pageNumber - number of page we are currently loading for
     */
    data class Loading<T : PagerItemType>(val pageNumber: Int, val pagerFlag: PagerFlags, override val currentItems: Map<Int, Pager.PagingItemsData<T>>) : PagerStates<T>(currentItems)

    /**
     * Error during paging
     *
     * @param T - type of items
     * @property throwable - error that was thrown during [Pager.getNextPage]
     * @property pagerFlag - flag we passed to [Pager.getNextPage]
     * @property currentItems - all the items we already loaded
     */
    data class Error<T : PagerItemType>(val throwable: Throwable, val pagerFlag: PagerFlags, override val currentItems: Map<Int, Pager.PagingItemsData<T>>) : PagerStates<T>(currentItems)

    /**
     * Success
     *
     * @param T - type of items
     * @property pagerFlag - flag we passed to [Pager.getNextPage]
     * @property currentItems - all the items we already loaded
     */
    data class Success<T : PagerItemType>(val pagerFlag: PagerFlags, override val currentItems: Map<Int, Pager.PagingItemsData<T>>) : PagerStates<T>(currentItems)

    /**
     * No more pages available
     *
     * @param T - type of items
     * @property currentItems - all the items we already loaded
     */
    data class NoMorePagesAvailable<T : PagerItemType>(override val currentItems: Map<Int, Pager.PagingItemsData<T>>) : PagerStates<T>(currentItems)
}

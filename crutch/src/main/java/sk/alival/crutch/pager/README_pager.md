# Pager
Custom implementation of pager to ease up the integration of paging

###  Basic setup
Basic setup required you to create a pagerItem, pager class, start listening for pager states, calling first page and notify pager about rendering.

```kotlin
data class PagerTestItem(
    val index: Int
) : PagerItemType {
    override val id: String
        get() = "$index"
}

class TestingPager : Pager<PagerTestItem>() {
    override val pageSize: AtomicInteger = AtomicInteger(2)
    override suspend fun getPage(pageNumber: Int): PagingItemsData<PagerTestItem> {
        return PagingItemsData(9, fetchDataFromApi(pageNumber))
    }
}

val testingPager = TestingPager()

// listen for pager states
testingPager.listenForPagingStates()
    .onEach {
        when (it) {
            is PagerStates.Error -> {
                // render error
            }
            is PagerStates.Loading -> {
                // render loading
            }
            is PagerStates.NoMorePagesAvailable -> {
                // render footer if needed
            }
            is PagerStates.Success -> {
                // render items
            }
        }
    }
    .launchIn(scope)

// triggers the first page call
testingPager.getFirstPage(scope = this)

// notify pager about what item index have been rendered so it can automatically trigger getPage for next page
testingPager.onItemRendered(index = 1, scope = this)
```

###  Advanced setup

**Custom page size**
override `pageSize` and define your own page size.

**Custom item offset**
override `itemOffsetBeforeNextPage` and define your own offset. Offset means, which item index will trigger the getPage call for next page.
Ideally it could be like 5 so your 5th item from the end will trigger new page.

**Debugging**
If you need to enable debugging and log everything that happens, use `PagerLogger` to enable debug mode.

**Swipe to refresh**
Calling `onSwipeToRefresh` will call getPage for page 1. If its success, it will clear other items. If it fails, it will just emit error and keep the existing items.

**Refreshing all pages**
calling `refreshAllPages` will refresh every loaded page using getPage.

**Resetting pager**
calling `cleanAll` will reset the pager state to state zero.
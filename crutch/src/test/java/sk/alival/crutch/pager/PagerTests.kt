package sk.alival.crutch.pager

import android.util.Log
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockkStatic
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
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
            CacheableDataLogger.isCacheableDataDebugModeEnabled = AtomicBoolean(true)
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
        // if we don't have totalPages
        assertEquals(5, testingPager.getPageFromItemNumber(10))
        // if we have totalPages
        testingPager.setCustomTotalPages(5)
        assertEquals(5, testingPager.getPageFromItemNumber(10))
    }

    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("Testing SwipeToRefresh")
    fun testSwipeToRefresh() = runTest {
        testingPager.listenForPagingStates().test {
            expectNoEvents()
            testingPager.onSwipeToRefresh(this, true)
            awaitItem().let {
                assert(it is PagerStates.Loading)
                assertEquals(it, PagerStates.Loading(1, PagerFlags.SwipeToRefresh, mapOf(1 to Pager.PagingItemsData(9, fetchDataFromApi(1)))))
            }
            awaitItem().let {
                assert(it is PagerStates.Success)
                assertEquals(it, PagerStates.Success(PagerFlags.SwipeToRefresh, mapOf(1 to Pager.PagingItemsData(9, fetchDataFromApi(1)))))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("Testing Initial fetch")
    fun testInitialFetch() = runTest {
        testingPager.listenForPagingStates().test {
            expectNoEvents()
            testingPager.getFirstPage(this, true)
            awaitItem().let {
                assert(it is PagerStates.Loading)
                assertEquals(it, PagerStates.Loading(1, PagerFlags.Initial, mapOf(1 to Pager.PagingItemsData(9, fetchDataFromApi(1)))))
            }
            awaitItem().let {
                assert(it is PagerStates.Success)
                assertEquals(it, PagerStates.Success(PagerFlags.Initial, mapOf(1 to Pager.PagingItemsData(9, fetchDataFromApi(1)))))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    @DisplayName("Testing paging")
    fun testPaging() = runTest {
        testingPager.listenForPagingStates().test {
            expectNoEvents()
            testingPager.getFirstPage(this, true)
            awaitItem().let {
                assert(it is PagerStates.Loading)
                assertEquals(it, PagerStates.Loading(1, PagerFlags.Initial, mapOf(1 to Pager.PagingItemsData(9, fetchDataFromApi(1)))))
            }
            awaitItem().let {
                assert(it is PagerStates.Success)
                assertEquals(it, PagerStates.Success(PagerFlags.Initial, mapOf(1 to Pager.PagingItemsData(9, fetchDataFromApi(1)))))
            }

            // ==========================================================================================
            testingPager.onItemRendered(index = 1, scope = this, isNetworkAvailable = true)
            awaitItem().let {
                assert(it is PagerStates.Loading)
                assertEquals(
                    it, PagerStates.Loading(
                        2,
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2))
                        )
                    )
                )
            }
            awaitItem().let {
                assert(it is PagerStates.Success)
                assertEquals(
                    it, PagerStates.Success(
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2))
                        )
                    )
                )
            }

            // ==========================================================================================
            testingPager.onItemRendered(index = 3, scope = this, isNetworkAvailable = true)
            awaitItem().let {
                assert(it is PagerStates.Loading)
                assertEquals(
                    it, PagerStates.Loading(
                        3,
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2)),
                            3 to Pager.PagingItemsData(9, fetchDataFromApi(3))
                        )
                    )
                )
            }
            awaitItem().let {
                assert(it is PagerStates.Success)
                assertEquals(
                    it, PagerStates.Success(
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2)),
                            3 to Pager.PagingItemsData(9, fetchDataFromApi(3))
                        )
                    )
                )
            }

            // ==========================================================================================
            testingPager.onItemRendered(index = 5, scope = this, isNetworkAvailable = true)
            awaitItem().let {
                Assertions.assertInstanceOf(PagerStates.Loading::class.java, it)
                assertEquals(
                    it, PagerStates.Loading(
                        4,
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2)),
                            3 to Pager.PagingItemsData(9, fetchDataFromApi(3)),
                            4 to Pager.PagingItemsData(9, fetchDataFromApi(4)),
                        )
                    )
                )
            }
            awaitItem().let {
                assert(it is PagerStates.Success)
                assertEquals(
                    it, PagerStates.Success(
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2)),
                            3 to Pager.PagingItemsData(9, fetchDataFromApi(3)),
                            4 to Pager.PagingItemsData(9, fetchDataFromApi(4)),
                        )
                    )
                )
            }

            // ==========================================================================================
            testingPager.onItemRendered(index = 7, scope = this, isNetworkAvailable = true)
            awaitItem().let {
                Assertions.assertInstanceOf(PagerStates.Loading::class.java, it)
                assertEquals(
                    it, PagerStates.Loading(
                        5,
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2)),
                            3 to Pager.PagingItemsData(9, fetchDataFromApi(3)),
                            4 to Pager.PagingItemsData(9, fetchDataFromApi(4)),
                            5 to Pager.PagingItemsData(9, fetchDataFromApi(5)),
                        )
                    )
                )
            }
            awaitItem().let {
                assert(it is PagerStates.Success)
                assertEquals(
                    it, PagerStates.Success(
                        PagerFlags.Paging, mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2)),
                            3 to Pager.PagingItemsData(9, fetchDataFromApi(3)),
                            4 to Pager.PagingItemsData(9, fetchDataFromApi(4)),
                            5 to Pager.PagingItemsData(9, fetchDataFromApi(5)),
                        )
                    )
                )
            }

            // ==========================================================================================
            testingPager.onItemRendered(index = 9, scope = this, isNetworkAvailable = true)
            awaitItem().let {
                Assertions.assertInstanceOf(PagerStates.NoMorePagesAvailable::class.java, it)
                assertEquals(
                    it, PagerStates.NoMorePagesAvailable(
                        mapOf(
                            1 to Pager.PagingItemsData(9, fetchDataFromApi(1)),
                            2 to Pager.PagingItemsData(9, fetchDataFromApi(2)),
                            3 to Pager.PagingItemsData(9, fetchDataFromApi(3)),
                            4 to Pager.PagingItemsData(9, fetchDataFromApi(4)),
                            5 to Pager.PagingItemsData(9, fetchDataFromApi(5)),
                        )
                    )
                )
            }
        }
    }
}

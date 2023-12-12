package sk.alival.crutch.pager

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class FlattenToItemListTest {

    data class TestItem(val a: String) : PagerItemType {
        override val id: String
            get() = a
    }

    @Test
    fun testFlattenToItemListWithEmptyMap() {
        val emptyMap = emptyMap<Int, Pager.PagingItemsData<TestItem>>()

        val actualItems = emptyMap.flattenToItemList()

        assertEquals(emptyList<TestItem>(), actualItems)
    }

    @TestFactory
    fun testFlattenToItemListWithNonEmptyMap() {
        val items = listOf(TestItem("Item1"), TestItem("Item2"), TestItem("Item3"))
        val pagingItemsData = mockk<Pager.PagingItemsData<TestItem>>(relaxed = true)
        every { pagingItemsData.items } returns items

        val map = hashMapOf(1 to pagingItemsData)

        val actualItems = map.flattenToItemList()

        assertEquals(items, actualItems)
    }
}

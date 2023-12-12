package sk.alival.crutch.pager

/**
 * Helper method to flatter the Map<PageNumber, Items> to a simple list
 *
 * @param T type of items
 * @return list of items
 */
fun <T : PagerItemType> Map<Int, Pager.PagingItemsData<T>>.flattenToItemList(): List<T> {
    return this
        .values
        .asSequence()
        .flatMap {
            it.items
        }.distinctBy { it.id }
        .toList()
}

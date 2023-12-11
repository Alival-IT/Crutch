package sk.alival.crutch.pager

/**
 * Marker interface for pager items.
 * Used to hold their Ids, and distinct by the Ids.
 *
 */
interface PagerItemType {
    /**
     * Unique Id of the item
     */
    val id: String
}

package sk.alival.crutch.pager

import androidx.annotation.Keep

/**
 * Pager flags to pass and retrieve from [Pager]
 *
 */
@Keep
enum class PagerFlags {
    SwipeToRefresh,
    Initial,
    Paging,
    RefreshingAllPages
}

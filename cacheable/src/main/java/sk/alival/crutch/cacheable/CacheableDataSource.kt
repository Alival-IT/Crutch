package sk.alival.crutch.cacheable

import androidx.annotation.Keep

/**
 * Cacheable data source indicating from where we got the data
 */
@Keep
enum class CacheableDataSource {
    CACHE,
    NEW
}

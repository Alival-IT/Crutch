package sk.alival.crutch.cacheable

import androidx.annotation.Keep

/**
 * Flow api result wrapper to return Error or Success from cache/new
 *
 * @param T of type we are getting
 */
@Keep
sealed class CacheableResultWrapper<T>(
    open val source: CacheableDataSource,
    open val key: CacheableUniqueCacheKey
) {
    /**
     * Success with data got from cache/new
     *
     * @param T - type of data
     * @property data - we got from cache/new
     */
    data class Success<T>(val data: T, override val source: CacheableDataSource, override val key: CacheableUniqueCacheKey) : CacheableResultWrapper<T>(source, key)

    /**
     * Error holding the throwable from new
     *
     * @param T - type of data
     * @property throwable - we got from new
     */
    data class Error<T>(val throwable: Throwable, override val source: CacheableDataSource, override val key: CacheableUniqueCacheKey) : CacheableResultWrapper<T>(source, key)

    /**
     * Empty state that can be emitted by empty cache
     *
     * @param T - type of data
     * @property source - from where we got the data
     */
    data class Empty<T>(override val source: CacheableDataSource, override val key: CacheableUniqueCacheKey) : CacheableResultWrapper<T>(source, key)
}

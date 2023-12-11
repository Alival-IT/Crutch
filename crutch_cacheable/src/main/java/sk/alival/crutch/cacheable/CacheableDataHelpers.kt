package sk.alival.crutch.cacheable

import sk.alival.crutch.logging.getNameForLogs

/**
 * Helper method to create prefs cache
 *
 * @param T - type of data to get/set
 * @param uniqueCacheKey - key for the data to get/set
 * @param newDataGetter - new call with return type [T] we are getting/setting from/to cache
 * @return [CacheableData] wrapping the new call
 */
inline fun <reified T : Any> CacheableDataCache.createCacheableData(uniqueCacheKey: CacheableUniqueCacheKey, noinline newDataGetter: suspend () -> T): CacheableData<T> {
    CacheableDataLogger.log { "Creating CacheableData for ${T::class.java.getNameForLogs()} with key $uniqueCacheKey" }
    return CacheableDataImpl(
        this,
        T::class,
        uniqueCacheKey,
        newDataGetter
    )
}

/**
 * Simplify getting of success data
 *
 * @param T - type of data
 * @return data from [CacheableResultWrapper.Success]
 */
fun <T> CacheableResultWrapper.Success<T>.getSuccessData(): T = this.data

/**
 * Simplify getting of error throwable
 *
 * @param T - type of data
 * @return throwable from [CacheableResultWrapper.Error]
 */
fun <T> CacheableResultWrapper.Error<T>.getErrorThrowable(): Throwable = this.throwable

/**
 * Get success data or null
 *
 * @param T - type of data
 * @return - data of type [T]
 */
fun <T> CacheableResultWrapper<T>.getSuccessDataOrNull(): T? = (this as? CacheableResultWrapper.Success?)?.data

/**
 * Get [CacheableResultWrapper.Success] or return [other]
 *
 * @param T - type of data
 * @param other - returned [CacheableResultWrapper] when [this] is not [CacheableResultWrapper.Success]
 * @return - [CacheableResultWrapper.Success] of this or [other]
 */
fun <T> CacheableResultWrapper<T>.getOrElse(other: CacheableResultWrapper<T>): CacheableResultWrapper<T> {
    return if (this is CacheableResultWrapper.Success) {
        this
    } else {
        other
    }
}

/**
 * [CacheableResultWrapper.Success] or null
 *
 * @param T - type of data
 * @return [CacheableResultWrapper.Success] or null
 */
fun <T> CacheableResultWrapper<T>.getSuccessOrNull(): CacheableResultWrapper.Success<T>? {
    return if (this is CacheableResultWrapper.Success<T>) {
        this
    } else {
        null
    }
}

/**
 * [CacheableResultWrapper.Success] or throws exception
 *
 * @param T - type of data
 * @return [CacheableResultWrapper.Success] or throws exception from [CacheableResultWrapper.Error] or throws [EmptyCacheableDataCacheException] for [CacheableResultWrapper.Empty]
 */
fun <T> CacheableResultWrapper<T>.getSuccessDataOrThrow(): CacheableResultWrapper.Success<T> {
    return when (this) {
        is CacheableResultWrapper.Empty -> throw EmptyCacheableDataCacheException(this.key)
        is CacheableResultWrapper.Error -> throw this.throwable
        is CacheableResultWrapper.Success -> this
    }
}

/**
 * Helper to pretty print [CacheableUniqueCacheKey]
 *
 */
fun CacheableUniqueCacheKey.toLogKeys() = "CacheKeys: ${this.mainKey}/${this.subKey}"

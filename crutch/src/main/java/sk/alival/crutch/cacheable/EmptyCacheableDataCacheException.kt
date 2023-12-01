package sk.alival.crutch.cacheable

/**
 * Exception thrown by [getSuccessDataOrThrow] when cache is empty
 *
 * @param key - indicating which key are we requesting
 */
class EmptyCacheableDataCacheException(key: CacheableUniqueCacheKey) : IllegalStateException("Empty Cache for CacheableUniqueCacheKey: ${key.mainKey} / ${key.subKey}")

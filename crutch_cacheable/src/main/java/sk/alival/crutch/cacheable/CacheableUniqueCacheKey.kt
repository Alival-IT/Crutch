package sk.alival.crutch.cacheable

/**
 * Cacheable unique cache key indicating under which key is the value stored
 */
interface CacheableUniqueCacheKey {
    /**
     * Main key for the cache, for example we cache userProfile, we can set it to "UserProfileCacheKey"
     */
    val mainKey: String

    /**
     * Sub key for the cache, for example we cache balance for each user, we can set it to unique userId
     */
    val subKey: String?
        get() = null
}

package sk.alival.crutch.cacheable

import kotlin.reflect.KClass

/**
 * Cache interface, which should be implemented before usage of [CacheableData] feature.
 * Usually its a key value storage like preferences or data store, but any other cache can be used.
 */
interface CacheableDataCache {
    /**
     * Get value from underlying cache
     *
     * @param T - type of value to get
     * @param clazz - KClass of class we are getting
     * @param key - unique key
     * @return - value or null, based on your cache implementation
     */
    suspend fun <T : Any> getValue(clazz: KClass<T>, key: CacheableUniqueCacheKey): T?

    /**
     * Set value to underlying cache
     *
     * @param T - type of value to get
     * @param clazz - KClass of class we are storing
     * @param key - unique key
     * @param value - value to store
     */
    suspend fun <T : Any> setValue(clazz: KClass<T>, key: CacheableUniqueCacheKey, value: T)
}

package sk.alival.crutch.cacheable

import kotlinx.coroutines.flow.Flow

/**
 * Cacheable data interface to manage what type of data we are requesting
 *
 * | method          	| CacheableResultWrapper.Success                	| CacheableResultWrapper.Error                                               	| CacheableResultWrapper.Empty                             	| Source        	|
 * |-----------------	|-----------------------------------------------	|----------------------------------------------------------------------------	|----------------------------------------------------------	|---------------	|
 * | getNew          	| returned if new was OK                        	| returned if new throws error                                               	| never returned                                           	| always NEW    	|
 * | getCached       	| returned if cache#getValue was OK and notNull 	| returned if cache#getValue throws error                                    	| returned if cache#getValue returns null                  	| always CACHE  	|
 * | getCachedOrNew  	| returned from getCached or getNew             	| returned from getNew                                                       	| never returned                                           	| CACHE or NEW  	|
 * | getCachedAndNew 	| returned from getCached and getNew            	| returned from getCached (only if emitOnlySuccessCache is false) and getNew 	| returned from getCached if emitOnlySuccessCache is false 	| CACHE and NEW 	|
 * | getNewOrCached  	| returned from getNew or getCached             	| returned from getCached                                                    	| returned if new is Error and cache#getValue returns null 	| NEW or CACHE  	|
 *
 * @param T - type of data we are getting
 */
interface CacheableData<T> {
    /**
     * Get new data
     *
     * @param cacheNewValue - indicating if we should cache the new data
     * @return [CacheableResultWrapper] holding data or error with source. In this case the source is always [CacheableDataSource.NEW]
     */
    suspend fun getNew(cacheNewValue: Boolean = true): CacheableResultWrapper<T>

    /**
     * Get cached value
     *
     * @return [CacheableResultWrapper] holding data, empty or error state depending on your cache implementation
     */
    suspend fun getCached(): CacheableResultWrapper<T>

    /**
     * Get cached or new of the cache is empty or returns error
     *
     * See [getNew] and [getCached]
     *
     * @param cacheNewValue - indicating if we should cache the new data
     * @return [CacheableResultWrapper]
     */
    suspend fun getCachedOrNew(cacheNewValue: Boolean = true): CacheableResultWrapper<T>

    /**
     * Get cached and new
     *
     * See [getNew] and [getCached]
     *
     * @param cacheNewValue - indicating if we should cache the new data
     * @param emitOnlySuccessCache - indicating if we should emit only success state from cache, or also emit error and empty state
     * @return [CacheableResultWrapper] cached data then new value
     */
    suspend fun getCachedAndNew(cacheNewValue: Boolean = true, emitOnlySuccessCache: Boolean = false): Flow<CacheableResultWrapper<T>>

    /**
     * Get new or cached
     *
     * See [getNew] and [getCached]
     *
     * @param cacheNewValue - indicating if we should cache the new data
     * @return [CacheableResultWrapper] get new if available, otherwise cached if available
     */
    suspend fun getNewOrCached(cacheNewValue: Boolean): CacheableResultWrapper<T>
}

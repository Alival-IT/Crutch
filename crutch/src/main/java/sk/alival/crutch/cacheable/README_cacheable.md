# CacheableData
Implementation to simplify wrapping datasource with support of cache and some convenient methods to get the new or cached values.

### Quick start

**Create cache**

1. Implement CacheableDataCache and provide your cache. Cache can be in-memory, sharedPreferences with serialization, database or any other storage.
```kotlin
    class ExampleCache : CacheableDataCache {
        private val cache: MutableMap<Pair<String, String?>, Any?> = mutableMapOf()

        override suspend fun <T : Any> getValue(clazz: KClass<T>, key: CacheableUniqueCacheKey): T? {
            return cache[Pair(key.mainKey, key.subKey)] as? T?
        }

        override suspend fun <T : Any> setValue(clazz: KClass<T>, key: CacheableUniqueCacheKey, value: T) {
            cache[Pair(key.mainKey, key.subKey)] = value
        }

        fun clearCache() {
            cache.clear()
        }
    }
```

2. Create your unique cacheKeys under which you will store/get your values.
```kotlin
    private object ExampleCacheKey : CacheableUniqueCacheKey {
        override val mainKey: String
            get() = "ExampleKey"
    }
```

3. Create your CacheableData with call of new data from your source. The usual source could be some API call.
```kotlin
    private val cacheableDataUserProfile = ExampleCache().createCacheableData(ExampleCacheKey) {
        apiService.getUserProfile()
    }
```

4. Finally, use methods to get new, cached or combination of those values.
```kotlin
    cacheableDataUserProfile.getNew()
    cacheableDataUserProfile.getCached()
    cacheableDataUserProfile.getCachedOrNew()
    cacheableDataUserProfile.getNewOrCached()
    cacheableDataUserProfile.getCachedAndNew()
```
### Return values
Each method can return a set of data types displayed in the table below. 

| method          	| CacheableResultWrapper.Success                	| CacheableResultWrapper.Error                                               	| CacheableResultWrapper.Empty                             	| Source        	|
|-----------------	|-----------------------------------------------	|----------------------------------------------------------------------------	|----------------------------------------------------------	|---------------	|
| getNew          	| returned if new was OK                        	| returned if new throws error                                               	| never returned                                           	| always NEW    	|
| getCached       	| returned if cache#getValue was OK and notNull 	| returned if cache#getValue throws error                                    	| returned if cache#getValue returns null                  	| always CACHE  	|
| getCachedOrNew  	| returned from getCached or getNew             	| returned from getNew                                                       	| never returned                                           	| CACHE or NEW  	|
| getCachedAndNew 	| returned from getCached and getNew            	| returned from getCached (only if emitOnlySuccessCache is false) and getNew 	| returned from getCached if emitOnlySuccessCache is false 	| CACHE and NEW 	|
| getNewOrCached  	| returned from getNew or getCached             	| returned from getCached                                                    	| returned if new is Error and cache#getValue returns null 	| NEW or CACHE  	|

You can use some of the extension methods to get the values or create your own.
```kotlin
    CacheableResultWrapper.Success.getSuccessData()
    CacheableResultWrapper.Error.getErrorThrowable()
    CacheableResultWrapper.getSuccessDataOrNull()
    CacheableResultWrapper.getOrElse()
    CacheableResultWrapper.getSuccessOrNull()
    CacheableResultWrapper.getSuccessDataOrThrow()
```
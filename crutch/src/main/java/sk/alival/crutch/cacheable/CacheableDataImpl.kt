package sk.alival.crutch.cacheable

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import sk.alival.crutch.cacheable.CacheableDataLogger.log
import sk.alival.crutch.logging.Logs
import sk.alival.crutch.logging.dm
import kotlin.reflect.KClass

class CacheableDataImpl<T : Any>(
    private val cache: CacheableDataCache,
    private val clazz: KClass<T>,
    private val key: CacheableUniqueCacheKey,
    private val new: suspend () -> T
) : CacheableData<T> {

    override suspend fun getNew(cacheNewValue: Boolean): CacheableResultWrapper<T> {
        CacheableDataLogger.log { Logs.dm { "Getting #getNew for ${clazz.simpleName} with key ${key.toLogKeys()} and cacheNewValue $cacheNewValue" } }
        return try {
            CacheableResultWrapper.Success(
                new(),
                CacheableDataSource.NEW,
                key
            ).also {
                if (cacheNewValue) {
                    cache.setValue(clazz, key, it.data)
                    CacheableDataLogger.log { Logs.dm { "Storing #getNew to cache ${clazz.simpleName} with key ${key.toLogKeys()} and cacheNewValue $cacheNewValue data ${it}" } }
                }
                CacheableDataLogger.log { Logs.dm { "Returning #getNew success for ${clazz.simpleName} with key ${key.toLogKeys()} and cacheNewValue $cacheNewValue data $it" } }
            }
        } catch (t: Throwable) {
            CacheableDataLogger.log { Logs.dm { "Returning #getNew error for ${clazz.simpleName} with key ${key.toLogKeys()} and cacheNewValue $cacheNewValue error $t" } }
            CacheableResultWrapper.Error(t, CacheableDataSource.NEW, key)
        }
    }

    override suspend fun getCached(): CacheableResultWrapper<T> {
        CacheableDataLogger.log { Logs.dm { "Getting #getCached for ${clazz.simpleName} with key ${key.toLogKeys()}" } }
        return try {
            (cache.getValue(clazz, key)?.run {
                CacheableResultWrapper.Success(
                    this,
                    CacheableDataSource.CACHE,
                    key
                )
            } ?: CacheableResultWrapper.Empty(CacheableDataSource.CACHE, key))
                .also {
                    CacheableDataLogger.log {
                        val dataType = if (it is CacheableResultWrapper.Empty) "Empty" else "Success"
                        Logs.dm { "Returning #getCached $dataType for ${clazz.simpleName} with key ${key.toLogKeys()} data $it" }
                    }
                }
        } catch (t: Throwable) {
            CacheableDataLogger.log { Logs.dm { "Returning #getCached error for ${clazz.simpleName} with key ${key.toLogKeys()} error $t" } }
            CacheableResultWrapper.Error(t, CacheableDataSource.CACHE, key)
        }
    }

    override suspend fun getCachedOrNew(cacheNewValue: Boolean): CacheableResultWrapper<T> {
        CacheableDataLogger.log { Logs.dm { "Getting #getCachedOrNew for ${clazz.simpleName} with key ${key.toLogKeys()} and cacheNewValue $cacheNewValue" } }
        return getCached().getOrElse(getNew(cacheNewValue))
    }

    override suspend fun getNewOrCached(cacheNewValue: Boolean): CacheableResultWrapper<T> {
        CacheableDataLogger.log { Logs.dm { "Getting #getNewOrCached for ${clazz.simpleName} with key ${key.toLogKeys()} and cacheNewValue $cacheNewValue" } }
        return getNew(cacheNewValue).getOrElse(getCached())
    }

    override suspend fun getCachedAndNew(cacheNewValue: Boolean, emitOnlySuccessCache: Boolean): Flow<CacheableResultWrapper<T>> {
        CacheableDataLogger.log { Logs.dm { "Getting #getCachedAndNew for ${clazz.simpleName} with key ${key.toLogKeys()} and cacheNewValue $cacheNewValue and emitOnlySuccessCache $emitOnlySuccessCache" } }
        return flowOf(
            if (emitOnlySuccessCache) {
                getCached().getSuccessOrNull()
            } else {
                getCached()
            },
            getNew()
        ).filterNotNull()
    }
}

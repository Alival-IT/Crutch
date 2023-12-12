package sk.alival.crutch.cacheable

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import sk.alival.crutch.logging.CustomLogs
import sk.alival.crutch.logging.Logs
import kotlin.reflect.KClass

class CacheableDataTests {

    private var newValueGetterData: String = "new abc"
    private var newValueGetter: NewValueGetter = mockk()
    private val cachedValue = "cached abc"
    private val clazz = String::class
    private val testCache = TestCache()
    private val cacheThrowable = IllegalAccessException("Cant read disk")

    inner class NewValueGetter {
        fun getNewValue() = newValueGetterData
    }

    inner class TestCache : CacheableDataCache {
        private val cache: MutableMap<Pair<String, String?>, Any?> = mutableMapOf()
        var throwCacheErrorForTesting: Boolean = false

        override suspend fun <T : Any> getValue(clazz: KClass<T>, key: CacheableUniqueCacheKey): T? {
            if (throwCacheErrorForTesting) {
                throw cacheThrowable
            } else {
                return cache[Pair(key.mainKey, key.subKey)] as? T?
            }
        }

        override suspend fun <T : Any> setValue(clazz: KClass<T>, key: CacheableUniqueCacheKey, value: T) {
            cache[Pair(key.mainKey, key.subKey)] = value
        }

        fun clearCache() {
            cache.clear()
        }
    }

    private object TestingCacheKey : CacheableUniqueCacheKey {
        override val mainKey: String
            get() = "TESTKEY"
    }

    private val cacheAbleImpl = testCache.createCacheableData(TestingCacheKey) {
        newValueGetter.getNewValue()
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            mockkStatic(Log::class)
            every { Log.println(any(), any(), any()) } returns 0
            Logs.init(true, customLogs = object : CustomLogs {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, tag, message, t)
                    println("$tag: $message $t")
                }
            })
            CacheableDataLogger.isCacheableDataDebugModeEnabled = AtomicBoolean(true)
        }
    }

    @BeforeEach
    fun cacheCleaner() {
        testCache.clearCache()
        testCache.throwCacheErrorForTesting = false
    }

    @Nested
    @DisplayName("GetCachedTests")
    inner class GetCachedTests {

        @Test
        @DisplayName("Cache not empty")
        fun testGetCachedSuccess(): Unit = runBlocking {
            testCache.setValue(clazz, TestingCacheKey, cachedValue)
            assertEquals(cachedValue, cacheAbleImpl.getCached().getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Cache empty")
        fun testGetCachedEmpty(): Unit = runBlocking {
            assertEquals(CacheableResultWrapper.Empty<String>(CacheableDataSource.CACHE, TestingCacheKey), cacheAbleImpl.getCached())
        }

        @Test
        @DisplayName("Cache error")
        fun testGetCachedError(): Unit = runBlocking {
            testCache.throwCacheErrorForTesting = true
            assertEquals(CacheableResultWrapper.Error<String>(cacheThrowable, CacheableDataSource.CACHE, TestingCacheKey), cacheAbleImpl.getCached())
        }
    }

    @Nested
    @DisplayName("GetNewTests")
    inner class GetNewTests {

        @Test
        @DisplayName("New not empty")
        fun testGetNewSuccess(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(newValueGetterData, cacheAbleImpl.getNew(false).getSuccessDataOrNull())
            assertEquals(newValueGetterData, cacheAbleImpl.getNew(true).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Cache New value")
        fun testGetNewCache(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(newValueGetterData, cacheAbleImpl.getNew(false).getSuccessDataOrNull())
            assertNotEquals(newValueGetterData, cacheAbleImpl.getCached().getSuccessDataOrNull())

            assertEquals(newValueGetterData, cacheAbleImpl.getNew(true).getSuccessDataOrNull())
            assertEquals(newValueGetterData, cacheAbleImpl.getCached().getSuccessDataOrNull())
        }

        @Test
        @DisplayName("New empty")
        fun testGetNewEmpty(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } throws IllegalAccessException("Not accessible test")
            assertFalse(cacheAbleImpl.getNew() is CacheableResultWrapper.Empty)
            assertInstanceOf(CacheableResultWrapper.Error::class.java, cacheAbleImpl.getNew())
            assertNotEquals(cacheAbleImpl.getNew(), CacheableResultWrapper.Empty<String>(CacheableDataSource.NEW, TestingCacheKey))
        }

        @Test
        @DisplayName("New error")
        fun testGetNewError(): Unit = runBlocking {
            val error = IllegalAccessException("Not accessible test")
            every { newValueGetter.getNewValue() } throws error
            assertInstanceOf(CacheableResultWrapper.Error::class.java, cacheAbleImpl.getNew())
            assertEquals(cacheAbleImpl.getNew(), CacheableResultWrapper.Error<String>(error, CacheableDataSource.NEW, TestingCacheKey))
        }
    }

    @Nested
    @DisplayName("GetCachedOrNewTests")
    inner class GetCachedOrNewTests {

        @Test
        @DisplayName("Cache empty api error")
        fun testGetCachedOrNewEmptyCacheApiError(): Unit = runBlocking {
            val error = IllegalAccessException("Not accessible test")
            every { newValueGetter.getNewValue() } throws error
            assertEquals(CacheableResultWrapper.Error<String>(error, CacheableDataSource.NEW, TestingCacheKey), cacheAbleImpl.getCachedOrNew(false))
            assertEquals(null, cacheAbleImpl.getCachedOrNew(false).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Cache empty api success")
        fun testGetCachedOrNewCacheNOkApiNok(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(newValueGetterData, cacheAbleImpl.getCachedOrNew(false).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Cache success api success")
        fun testGetCachedOrNewCacheOkApiOk(): Unit = runBlocking {
            testCache.setValue(clazz, TestingCacheKey, cachedValue)
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(cachedValue, cacheAbleImpl.getCachedOrNew(false).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Cache success api error")
        fun testGetCachedOrNewCacheOkApiNok(): Unit = runBlocking {
            testCache.setValue(clazz, TestingCacheKey, cachedValue)
            val error = IllegalAccessException("Not accessible test")
            every { newValueGetter.getNewValue() } throws error
            assertEquals(cachedValue, cacheAbleImpl.getCachedOrNew(false).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Getting new then cached")
        fun testGetCachedOrNewNewThenCached(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(newValueGetterData, cacheAbleImpl.getCachedOrNew(true).getSuccessDataOrNull())

            val cachedNewValue = cacheAbleImpl.getCachedOrNew(false)
            assertEquals(newValueGetterData, cachedNewValue.getSuccessDataOrNull())
            assertEquals(CacheableDataSource.CACHE, cachedNewValue.source)
        }
    }

    @Nested
    @DisplayName("GetNewOrCachedTests")
    inner class GetNewOrCachedTests {

        @Test
        @DisplayName("Cache empty api error")
        fun testGetNewOrCachedEmptyCacheApiError(): Unit = runBlocking {
            val error = IllegalAccessException("Not accessible test")
            every { newValueGetter.getNewValue() } throws error
            assertEquals(CacheableResultWrapper.Empty<String>(CacheableDataSource.CACHE, TestingCacheKey), cacheAbleImpl.getNewOrCached(false))
            assertEquals(null, cacheAbleImpl.getNewOrCached(false).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Cache empty api success")
        fun testGetNewOrCachedCacheNOkApiNok(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(newValueGetterData, cacheAbleImpl.getNewOrCached(false).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Cache success api success")
        fun testGetNewOrCachedCacheOkApiOk(): Unit = runBlocking {
            testCache.setValue(clazz, TestingCacheKey, cachedValue)
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(newValueGetterData, cacheAbleImpl.getNewOrCached(false).getSuccessDataOrNull())
            assertEquals(CacheableDataSource.NEW, cacheAbleImpl.getNewOrCached(false).source)
        }

        @Test
        @DisplayName("Cache success api error")
        fun testGetNewOrCachedCacheOkApiNok(): Unit = runBlocking {
            testCache.setValue(clazz, TestingCacheKey, cachedValue)
            val error = IllegalAccessException("Not accessible test")
            every { newValueGetter.getNewValue() } throws error
            assertEquals(cachedValue, cacheAbleImpl.getNewOrCached(false).getSuccessDataOrNull())
        }

        @Test
        @DisplayName("Getting new then cached")
        fun testGetNewOrCachedNewThenCached(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData
            assertEquals(newValueGetterData, cacheAbleImpl.getNewOrCached(true).getSuccessDataOrNull())

            val cachedNewValue = cacheAbleImpl.getNewOrCached(false)
            assertEquals(newValueGetterData, cachedNewValue.getSuccessDataOrNull())
            assertEquals(CacheableDataSource.NEW, cachedNewValue.source)
        }
    }

    @Nested
    @DisplayName("GetCachedAndNewTests")
    inner class GetCachedAndNewTests {

        @Test
        @DisplayName("Cache empty api error")
        fun testGetCachedAndNewEmptyCacheApiError(): Unit = runBlocking {
            val error = IllegalAccessException("Not accessible test")
            every { newValueGetter.getNewValue() } throws error

            assertEquals(
                cacheAbleImpl.getCachedAndNew(true, true)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(null)
            )

            assertEquals(
                cacheAbleImpl.getCachedAndNew(true, false)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(null, null)
            )
        }

        @Test
        @DisplayName("Cache empty api success")
        fun testGetCachedAndNewCacheNOkApiNok(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData

            assertEquals(
                cacheAbleImpl.getCachedAndNew(cacheNewValue = true, emitOnlySuccessCache = true)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(newValueGetterData)
            )

            assertEquals(
                cacheAbleImpl.getCachedAndNew(cacheNewValue = true, emitOnlySuccessCache = false)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(newValueGetterData, newValueGetterData)
            )
        }

        @Test
        @DisplayName("Cache success api success")
        fun testGetCachedAndNewCacheOkApiOk(): Unit = runBlocking {
            testCache.setValue(clazz, TestingCacheKey, cachedValue)
            every { newValueGetter.getNewValue() } returns newValueGetterData

            assertEquals(
                cacheAbleImpl.getCachedAndNew(cacheNewValue = true, emitOnlySuccessCache = true)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(cachedValue, newValueGetterData)
            )
        }

        @Test
        @DisplayName("Cache success api error")
        fun testGetCachedAndNewCacheOkApiNok(): Unit = runBlocking {
            testCache.setValue(clazz, TestingCacheKey, cachedValue)
            val error = IllegalAccessException("Not accessible test")
            every { newValueGetter.getNewValue() } throws error

            assertEquals(
                cacheAbleImpl.getCachedAndNew(true)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(cachedValue, null)
            )
        }

        @Test
        @DisplayName("Getting new then cached")
        fun testGetCachedAndNewThenCached(): Unit = runBlocking {
            every { newValueGetter.getNewValue() } returns newValueGetterData

            assertEquals(
                cacheAbleImpl.getCachedAndNew(true)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(null, newValueGetterData)
            )

            assertEquals(
                cacheAbleImpl.getCachedAndNew(true)
                    .map {
                        it.getSuccessDataOrNull()
                    }.toList(), listOf(newValueGetterData, newValueGetterData)
            )
        }
    }
}

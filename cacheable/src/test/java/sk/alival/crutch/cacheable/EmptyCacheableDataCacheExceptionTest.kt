package sk.alival.crutch.cacheable

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EmptyCacheableDataCacheExceptionTest {

    @Test
    fun testEmptyCacheableDataCacheExceptionWithMainKey() {
        val key = object : CacheableUniqueCacheKey {
            override val mainKey: String
                get() = "mainKey"
            override val subKey: String
                get() = "subKey"
        }
        val exception = EmptyCacheableDataCacheException(key)

        assertEquals("Empty Cache for CacheableUniqueCacheKey: mainKey / subKey", exception.message)
    }

    @Test
    fun testEmptyCacheableDataCacheExceptionWithNullSubKey() {
        val key = object : CacheableUniqueCacheKey {
            override val mainKey: String
                get() = "mainKey"
            override val subKey: String?
                get() = null
        }
        val exception = EmptyCacheableDataCacheException(key)

        assertEquals("Empty Cache for CacheableUniqueCacheKey: mainKey / null", exception.message)
    }

    @Test
    fun testEmptyCacheableDataCacheExceptionWithBothNullMainAndSubKey() {
        val key = object : CacheableUniqueCacheKey {
            override val mainKey: String
                get() = "mainKey"
        }
        val exception = EmptyCacheableDataCacheException(key)

        assertEquals("Empty Cache for CacheableUniqueCacheKey: mainKey / null", exception.message)
    }
}

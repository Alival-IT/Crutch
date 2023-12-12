package sk.alival.crutch.cacheable

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CacheableResultWrapperTest {

    @Test
    fun testGetSuccessData() {
        val successData = mockk<CharSequence>()
        val successResult = CacheableResultWrapper.Success(successData, mockk(), mockk())

        val actualData = successResult.getSuccessData()

        assertEquals(successData, actualData)
    }

    @Test
    fun testGetSuccessDataWithNull() {
        val successResult = CacheableResultWrapper.Error<String>(IllegalStateException(), mockk(), mockk())

        val actualData = (successResult as CacheableResultWrapper<String>).getSuccessDataOrNull()

        assertEquals(null, actualData)
    }

    @Test
    fun testGetErrorThrowable() {
        val throwable = mockk<Throwable>()
        val errorResult = CacheableResultWrapper.Error<String>(throwable, mockk(), mockk())

        val actualThrowable = errorResult.getErrorThrowable()

        assertEquals(throwable, actualThrowable)
    }

    @Test
    fun testGetSuccessDataOrThrowForEmptyCache() {
        val emptyResult = CacheableResultWrapper.Empty<String>(mockk(), mockk())

        assertThrows(EmptyCacheableDataCacheException::class.java) {
            emptyResult.getSuccessDataOrThrow()
        }
    }

    @Test
    fun testGetSuccessDataOrThrowForErrorCache() {
        val throwable = mockk<Throwable>()
        val errorResult = CacheableResultWrapper.Error<CharSequence>(throwable, mockk(), mockk())

        assertThrows(throwable::class.java) {
            errorResult.getSuccessDataOrThrow()
        }
    }

    @Test
    fun testGetSuccessDataOrThrowForSuccessCache() {
        val successData = mockk<CharSequence>()
        val successResult = CacheableResultWrapper.Success(successData, mockk(), mockk())

        val actualResult = successResult.getSuccessDataOrThrow()

        assertEquals(successResult, actualResult)
    }
}

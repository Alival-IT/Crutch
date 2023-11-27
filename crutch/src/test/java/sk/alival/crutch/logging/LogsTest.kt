package sk.alival.crutch.logging

import android.util.Log
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import timber.log.Timber

class LogsTest {

    private val testTree = TestTree()

    class TestTree : Timber.Tree() {
        val logs = mutableListOf<Log>()

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            logs.add(Log(priority, tag, message, t))
        }

        data class Log(val priority: Int, val tag: String?, val message: String?, val t: Throwable?)
    }

    @BeforeEach
    fun init() {
        Timber.plant(testTree)
        Logs.disableDefaultLogs()
        mockkStatic(Log::class)
        every { Log.println(any(), any(), any()) } returns 0
    }

    private fun verifyMockedCustomLogs(customLogs: CustomLogs, numberOfInvocations: Int) {
        val exception = RuntimeException()

        val l1 = Logs.et { exception }
        assertEquals(l1, Logs)
        verify(exactly = numberOfInvocations) {
            customLogs.et(any(), any())
        }

        val l2 = Logs.dt { exception }
        assertEquals(l2, Logs)
        verify(exactly = numberOfInvocations) {
            customLogs.dt(any(), any())
        }

        val l3 = Logs.em { "" }
        assertEquals(l3, Logs)
        verify(exactly = numberOfInvocations) {
            customLogs.em(any(), any())
        }

        val l4 = Logs.dm { "" }
        assertEquals(l4, Logs)
        verify(exactly = numberOfInvocations) {
            customLogs.dm(any(), any())
        }
    }

    private fun verifyDefaultLogsWithoutInit() {
        val exception = RuntimeException()

        Logs.et(tag = "abc") { exception }
        assertEquals(0, testTree.logs.size)

        Logs.dt(tag = "abc") { exception }
        assertEquals(0, testTree.logs.size)

        Logs.wt(tag = "abc") { exception }
        assertEquals(0, testTree.logs.size)

        Logs.em(tag = "abc") { "def" }
        assertEquals(0, testTree.logs.size)

        Logs.dm(tag = "abc") { "def" }
        assertEquals(0, testTree.logs.size)

        Logs.wm(tag = "abc") { "def" }
        assertEquals(0, testTree.logs.size)
    }

    @ParameterizedTest(name = "Testing CustomLogs invocations with default enabled: {0}")
    @ValueSource(booleans = [true, false])
    fun testCustomLogs(enableDefault: Boolean) {
        val mockedCustomLogs = mockk<CustomLogs>()
        Logs.init(enableDefault, mockedCustomLogs)
        verifyMockedCustomLogs(mockedCustomLogs, 1)
    }

    @Test
    @DisplayName("Testing null CustomLogs")
    fun testNullCustomLogs() {
        Logs.init(true, null)
        assertNull(Logs.customLogs)
    }

    @Test
    @DisplayName("Testing CustomLogs without Logs#init")
    fun customLogsWithoutInit() {
        val mockedCustomLogs = mockk<CustomLogs>()
        verifyMockedCustomLogs(mockedCustomLogs, 0)
        verifyDefaultLogsWithoutInit()
    }

    @Test
    @DisplayName("Verify disable and isDisabled calls for DefaultLogs")
    fun testDisabled() {
        Logs.init(true)
        assertTrue(Logs.isDefaultLogsEnabled())

        Logs.disableDefaultLogs()
        assertFalse(Logs.isDefaultLogsEnabled())
    }

    @Test
    @DisplayName("CustomLogs#log called on every logging type with tags")
    fun testLog() {
        val mockedCustomLogs = mockk<CustomLogs>()
        Logs.init(true, mockedCustomLogs)

        //Testing dm
        Logs.dm(tag = "dmTag") { "abc" }
        verify(exactly = 1) {
            mockedCustomLogs.log(any(), "dmTag", "abc", any())
        }
        clearMocks(mockedCustomLogs)

        //Testing dt
        val t1 = IllegalStateException("Test exception dt")
        Logs.dt(tag = "dtTag") { t1 }
        verify(exactly = 1) {
            mockedCustomLogs.log(any(), "dtTag", any(), t1)
        }
        clearMocks(mockedCustomLogs)


        //Testing em
        Logs.em(tag = "emTag") { "def" }
        verify(exactly = 1) {
            mockedCustomLogs.log(any(), "emTag", "def", any())
        }
        clearMocks(mockedCustomLogs)

        //Testing et
        val t2 = IllegalStateException("Test exception et")
        Logs.et(tag = "etTag") { t2 }
        verify(exactly = 1) {
            mockedCustomLogs.log(any(), "etTag", any(), t2)
        }
        clearMocks(mockedCustomLogs)


        //Testing wm
        Logs.wm(tag = "wmTag") { "ghi" }
        verify(exactly = 1) {
            mockedCustomLogs.log(any(), "wmTag", "ghi", any())
        }
        clearMocks(mockedCustomLogs)

        //Testing wt
        val t3 = IllegalStateException("Test exception wt")
        Logs.wt(tag = "wtTag") { t3 }
        verify(exactly = 1) {
            mockedCustomLogs.log(any(), "wtTag", any(), t3)
        }
        clearMocks(mockedCustomLogs)
    }

    @ParameterizedTest(name = "Test Logs#isCustomLogsEnabled")
    @ValueSource(booleans = [true, false])
    fun isCustomEnabled(enabled: Boolean) {
        val mockedCustomLogs = mockk<CustomLogs>()
        if (enabled) {
            Logs.init(true, mockedCustomLogs)
            assertTrue(Logs.isCustomLogsEnabled())
        } else {
            Logs.init(true, null)
            assertFalse(Logs.isCustomLogsEnabled())
        }
    }

    @ParameterizedTest(name = "Test using tag")
    @ValueSource(booleans = [true, false])
    fun tagsTest(useTag: Boolean) {
        val mockedCustomLogs = mockk<CustomLogs>()
        Logs.init(true, mockedCustomLogs)

        if (useTag) {
            Logs.dm("dmTag") { "dmMessage" }
            verify(exactly = 1) {
                mockedCustomLogs.log(any(), "dmTag", "dmMessage", any())
            }
        } else {
            Logs.dm { "dmMessage" }
            verify(exactly = 1) {
                mockedCustomLogs.log(any(), any(), "dmMessage", any())
            }
        }
    }
}

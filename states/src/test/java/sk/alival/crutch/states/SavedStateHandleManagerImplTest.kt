package sk.alival.crutch.states

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import io.mockk.spyk
import java.io.Serializable
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sk.alival.crutch.states.savedStateHandleManager.SavedStateHandleManagerImpl

class SavedStateHandleManagerImplTest : BaseStatesTest() {

    private val testKey1 = "testKey1"
    private val savedStateHandle: SavedStateHandle = spyk(SavedStateHandle())
    private val savedStateHandleManager = SavedStateHandleManagerImpl(savedStateHandle)

    data class SavedStateSerializableTester(val s: String) : Serializable {
        companion object {
            private const val serialVersionUID: Long = 8238907128795
        }
    }

    @Parcelize
    data class SavedStateParcelableTester(val s: String) : Parcelable

    data class SavedStateDataTester(val s: String)

    @BeforeEach
    fun setup() {
        savedStateHandle.keys().forEach {
            savedStateHandle.remove<Any>(it)
        }
    }

    @Test
    fun testSavingWithSupportedTypes() {
        with("TestingValue") {
            savedStateHandleManager.setValue(testKey1, this)
            assertEquals(this, savedStateHandleManager.getValue(testKey1))
        }

        savedStateHandleManager.removeValue<String>(testKey1)

        with(SavedStateSerializableTester("bshgsd35d")) {
            savedStateHandleManager.setValue(testKey1, this)
            assertEquals(this, savedStateHandleManager.getValue(testKey1))
        }

        savedStateHandleManager.removeValue<SavedStateSerializableTester>(testKey1)

        with(SavedStateParcelableTester("354213414")) {
            savedStateHandleManager.setValue(testKey1, this)
            assertEquals(this, savedStateHandleManager.getValue(testKey1))
        }
    }

    @Test
    fun testSavingWithUnSupportedTypes() {
        with(SavedStateDataTester("257364754634")) {
            savedStateHandleManager.setValue(testKey1, this)
            assertNotEquals(this, savedStateHandleManager.getValue(testKey1))
            assertNull(savedStateHandleManager.getValue(testKey1))
        }
    }
}

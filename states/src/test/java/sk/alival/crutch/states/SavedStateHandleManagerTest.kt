package sk.alival.crutch.states

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import sk.alival.crutch.states.endings.emitViewState
import sk.alival.crutch.states.savedStateHandleManager.SavedStateHandleManagerImpl
import sk.alival.crutch.states.streams.findViewStateStreamByType
import sk.alival.crutch.states.streams.registerCustomViewState
import sk.alival.crutch.states.streams.unRegisterCustomViewState

class SavedStateHandleManagerTest : BaseStatesTest() {

    private val savedStateHandle: SavedStateHandle = spyk(SavedStateHandle())
    private val mockedSavedStateHandle: SavedStateHandle = mockk()


    private val key1 = "key1"
    private val key2 = "key2"

    private val testViewModel by lazy { StatesTestViewModel(StatesTestViewState(isLoading = false), savedStateHandle, key1) }
    private val testViewModelMockedHandle by lazy { StatesTestViewModel(StatesTestViewState(isLoading = false), mockedSavedStateHandle, key1) }
    private val testViewModelWithoutKey by lazy { StatesTestViewModel(StatesTestViewState(isLoading = false), savedStateHandle) }

    private val testStatesModel by lazy { StatesTestStatesModel(TestScope(), StatesTestViewState(), SavedStateHandleManagerImpl(savedStateHandle)) }

    @Nested
    @DisplayName("SavedStateHandle viewModels")
    inner class SavedStateHandleManagerTestViewModel {

        @Test
        fun testSavedStateHandleViewModelSuccessFlow() = runTest {
            // test default state and emit new
            testViewModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testViewModel.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // test unregister
            testViewModel.unRegisterCustomViewState<StatesTestViewState>()
            Assertions.assertNull(testViewModel.findViewStateStreamByType<StatesTestViewState>())

            // register new
            testViewModel.registerCustomViewState(StatesTestViewState(), key1)
            Assertions.assertNotNull(testViewModel.findViewStateStreamByType<StatesTestViewState>())

            testViewModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // register existing
            testViewModel.registerCustomViewState(StatesTestViewState(), key1)
            Assertions.assertNotNull(testViewModel.findViewStateStreamByType<StatesTestViewState>())

            testViewModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }
        }

        @Test
        fun testSavedStateHandleViewModelRegisterWithDifferentKey() = runTest {
            // test default state and emit new
            testViewModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testViewModel.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // register existing with different key
            testViewModel.registerCustomViewState(StatesTestViewState(), key2)
            Assertions.assertNotNull(testViewModel.findViewStateStreamByType<StatesTestViewState>())

            testViewModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                // since we registered with different key, we should not get the previous value
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())
            }
        }

        @Test
        fun testSavedStateHandleViewModelRegisterWithoutKey() = runTest {
            // test default state and emit new
            testViewModelWithoutKey.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testViewModelWithoutKey.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // test unregister
            testViewModelWithoutKey.unRegisterCustomViewState<StatesTestViewState>()
            Assertions.assertNull(testViewModelWithoutKey.findViewStateStreamByType<StatesTestViewState>())

            // register new
            testViewModelWithoutKey.registerCustomViewState(StatesTestViewState())
            Assertions.assertNotNull(testViewModelWithoutKey.findViewStateStreamByType<StatesTestViewState>())

            testViewModelWithoutKey.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // register existing
            testViewModelWithoutKey.registerCustomViewState(StatesTestViewState())
            Assertions.assertNotNull(testViewModelWithoutKey.findViewStateStreamByType<StatesTestViewState>())

            testViewModelWithoutKey.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }
        }

        @Test
        fun testSavedStateHandleViewModelErrorGet() = runTest {
            // test default state and emit new
            every { mockedSavedStateHandle.get<Any>(any()) } returns null
            testViewModelMockedHandle.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                every { mockedSavedStateHandle.set<Any>(any(), any()) } just runs

                testViewModelMockedHandle.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // register existing with different key
            every { mockedSavedStateHandle.get<Any>(any()) } throws (IllegalStateException("Invalid key and type for Testing"))
            testViewModelMockedHandle.registerCustomViewState(StatesTestViewState(), key1)
            Assertions.assertNotNull(testViewModelMockedHandle.findViewStateStreamByType<StatesTestViewState>())

            testViewModelMockedHandle.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                // since we registered and get returned error, we should not get the previous value
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())
            }
        }

        @Test
        fun testSavedStateHandleViewModelErrorSet() = runTest {
            // test default state and emit new
            every { mockedSavedStateHandle.get<Any>(any()) } returns null
            testViewModelMockedHandle.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                every { mockedSavedStateHandle.set<Any>(any(), any()) } throws (IllegalStateException("Invalid key and type for Testing"))

                testViewModelMockedHandle.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }
        }
    }

    @Nested
    @DisplayName("SavedStateHandle statesModel")
    inner class SavedStateHandleManagerTestStatesModel {
        @Test
        fun testSavedStateHandleStatesModelSuccessFlow() = runTest {
            testStatesModel.registerCustomViewState(StatesTestViewState(), key1)
            // test default state and emit new
            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testStatesModel.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // test unregister
            testStatesModel.unRegisterCustomViewState<StatesTestViewState>()
            Assertions.assertNull(testStatesModel.findViewStateStreamByType<StatesTestViewState>())

            // register new
            testStatesModel.registerCustomViewState(StatesTestViewState(), key1)
            Assertions.assertNotNull(testStatesModel.findViewStateStreamByType<StatesTestViewState>())

            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // register existing
            testStatesModel.registerCustomViewState(StatesTestViewState(), key1)
            Assertions.assertNotNull(testStatesModel.findViewStateStreamByType<StatesTestViewState>())

            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }
        }

        @Test
        fun testSavedStateHandleStatesModelRegisterWithDifferentKey() = runTest {
            testStatesModel.registerCustomViewState(StatesTestViewState(), key1)
            // test default state and emit new
            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testStatesModel.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // register existing with different key
            testStatesModel.registerCustomViewState(StatesTestViewState(), key2)
            Assertions.assertNotNull(testStatesModel.findViewStateStreamByType<StatesTestViewState>())

            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                // since we registered with different key, we should not get the previous value
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())
            }
        }

        @Test
        fun testSavedStateHandleStatesModelRegisterWithoutKey() = runTest {
            testStatesModel.registerCustomViewState(StatesTestViewState())
            // test default state and emit new
            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testStatesModel.emitViewState { it.copy(isLoading = true) }
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // test unregister
            testStatesModel.unRegisterCustomViewState<StatesTestViewState>()
            Assertions.assertNull(testStatesModel.findViewStateStreamByType<StatesTestViewState>())

            // register new
            testStatesModel.registerCustomViewState(StatesTestViewState())
            Assertions.assertNotNull(testStatesModel.findViewStateStreamByType<StatesTestViewState>())

            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }

            // register existing
            testStatesModel.registerCustomViewState(StatesTestViewState())
            Assertions.assertNotNull(testStatesModel.findViewStateStreamByType<StatesTestViewState>())

            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                Assertions.assertEquals(StatesTestViewState(isLoading = true), awaitItem())
            }
        }
    }
}

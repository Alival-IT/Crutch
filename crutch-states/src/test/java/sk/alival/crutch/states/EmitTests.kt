@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package sk.alival.crutch.states

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import sk.alival.crutch.states.endings.emitEvent
import sk.alival.crutch.states.endings.emitViewState
import sk.alival.crutch.states.endings.emitViewStateWithType
import sk.alival.crutch.states.streams.findEventByType
import sk.alival.crutch.states.streams.findViewStateStreamByType
import sk.alival.crutch.states.streams.registerCustomViewState

class EmitTests : BaseStatesTest() {

    private val testViewModel by lazy { StatesTestViewModel(StatesTestViewState(isLoading = false)) }
    private val testStatesModel by lazy { StatesTestStatesModel(TestScope(), StatesTestViewState()) }

    @Nested
    @DisplayName("Testing view states on ViewModel")
    inner class EmitViewStateViewModel {

        @Test
        @DisplayName("Testing emit default state")
        fun testEmitViewState(): Unit = runTest {
            testViewModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testViewModel.emitViewState { it.copy(isLoading = true) }
                assertEquals(StatesTestViewState(isLoading = true), awaitItem())

                testViewModel.emitViewState { it.copy(isLoading = true) }
                expectNoEvents()
            }
        }

        @Test
        @DisplayName("Testing emit custom state")
        fun testEmitCustomState(): Unit = runTest {
            testViewModel.registerCustomViewState(StatesTestCustomViewState(false))
            testViewModel.findViewStateStreamByType<StatesTestCustomViewState>().getOrFail().test {
                assertEquals(StatesTestCustomViewState(isLoading2 = false), awaitItem())

                testViewModel.emitViewStateWithType<StatesTestCustomViewState> { StatesTestCustomViewState(isLoading2 = true) }
                assertEquals(StatesTestCustomViewState(isLoading2 = true), awaitItem())

                testViewModel.emitViewStateWithType<StatesTestCustomViewState> { StatesTestCustomViewState(isLoading2 = true) }
                expectNoEvents()
            }
        }
    }

    @Nested
    @DisplayName("Testing view states on StatesModel")
    inner class EmitViewStateStatesModel {

        @Test
        @DisplayName("Testing emit default state")
        fun testEmitViewState(): Unit = runTest {
            testStatesModel.findViewStateStreamByType<StatesTestViewState>().getOrFail().test {
                assertEquals(StatesTestViewState(isLoading = false), awaitItem())

                testStatesModel.emitViewState { it.copy(isLoading = true) }
                assertEquals(StatesTestViewState(isLoading = true), awaitItem())

                testStatesModel.emitViewState { it.copy(isLoading = true) }
                expectNoEvents()
            }
        }

        @Test
        @DisplayName("Testing emit custom state")
        fun testEmitCustomState(): Unit = runTest {
            testStatesModel.registerCustomViewState(StatesTestCustomViewState(false))
            testStatesModel.findViewStateStreamByType<StatesTestCustomViewState>().getOrFail().test {
                assertEquals(StatesTestCustomViewState(isLoading2 = false), awaitItem())

                testStatesModel.emitViewStateWithType<StatesTestCustomViewState> { StatesTestCustomViewState(isLoading2 = true) }
                assertEquals(StatesTestCustomViewState(isLoading2 = true), awaitItem())

                testStatesModel.emitViewStateWithType<StatesTestCustomViewState> { StatesTestCustomViewState(isLoading2 = true) }
                expectNoEvents()
            }
        }
    }

    @Nested
    @DisplayName("Testing events on ViewModel")
    inner class EmitEventViewModel {
        @Test
        @DisplayName("Testing emit event")
        fun testEmitViewState(): Unit = runTest {
            testViewModel.findEventByType<StatesTestCustomEvent>().getOrFail().test {
                testViewModel.emitEvent(StatesTestCustomEvent(isLoading = true))
                assertEquals(StatesTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testViewModel.emitEvent(StatesTestCustomEvent(isLoading = true))
                assertEquals(StatesTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testViewModel.emitEvent(StatesTestCustomEvent(isLoading = false))
                assertEquals(StatesTestCustomEvent(isLoading = false), awaitItem().getContentIfNotHandled())
            }
        }
    }

    @Nested
    @DisplayName("Testing events on StatesModel")
    inner class EmitEventStatesModel {
        @Test
        @DisplayName("Testing emit event")
        fun testEmitViewState(): Unit = runTest {
            testStatesModel.findEventByType<StatesTestCustomEvent>().getOrFail().test {

                testStatesModel.emitEvent(StatesTestCustomEvent(isLoading = true))
                assertEquals(StatesTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testStatesModel.emitEvent(StatesTestCustomEvent(isLoading = true))
                assertEquals(StatesTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testStatesModel.emitEvent(StatesTestCustomEvent(isLoading = false))
                assertEquals(StatesTestCustomEvent(isLoading = false), awaitItem().getContentIfNotHandled())
            }
        }
    }
}

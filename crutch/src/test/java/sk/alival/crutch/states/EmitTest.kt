@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package sk.alival.crutch.states

import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
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
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.streams.StatesStreamsContainer
import sk.alival.crutch.states.streams.findEventByType
import sk.alival.crutch.states.streams.findViewStateStreamByType
import sk.alival.crutch.states.streams.registerCustomEvent
import sk.alival.crutch.states.streams.registerCustomViewState

internal data class EmitTestViewState(
    val isLoading: Boolean = false
)

internal data class EmitTestCustomViewState(
    val isLoading2: Boolean = false
)

internal data class EmitTestCustomEvent(
    val isLoading: Boolean = false
) : StatesOneTimeEvents

internal class TestViewModel(defaultState: EmitTestViewState) : StatesViewModel<EmitTestViewState>(defaultState) {
    init {
        registerCustomEvent<EmitTestCustomEvent>()
    }
}

internal class TestStatesModel(scope: CoroutineScope, defaultState: EmitTestViewState) : States<EmitTestViewState> {

    override val statesStreamsContainer: StatesStreamsContainer = StatesStreamsContainer(scope)

    init {
        registerCustomViewState(defaultState)
        registerCustomEvent<EmitTestCustomEvent>()
    }
}

class EmitTest : BaseStatesTest() {

    private val testViewModel by lazy { TestViewModel(EmitTestViewState(isLoading = false)) }
    private val testStatesModel by lazy { TestStatesModel(TestScope(), EmitTestViewState()) }

    @Nested
    @DisplayName("Testing view states on ViewModel")
    inner class EmitViewStateViewModel {

        @Test
        @DisplayName("Testing emit default state")
        fun testEmitViewState(): Unit = runTest {
            testViewModel.findViewStateStreamByType<EmitTestViewState>().getOrFail().test {
                assertEquals(EmitTestViewState(isLoading = false), awaitItem())

                testViewModel.emitViewState { it.copy(isLoading = true) }
                assertEquals(EmitTestViewState(isLoading = true), awaitItem())

                testViewModel.emitViewState { it.copy(isLoading = true) }
                expectNoEvents()
            }
        }

        @Test
        @DisplayName("Testing emit custom state")
        fun testEmitCustomState(): Unit = runTest {
            testViewModel.registerCustomViewState(EmitTestCustomViewState(false))
            testViewModel.findViewStateStreamByType<EmitTestCustomViewState>().getOrFail().test {
                assertEquals(EmitTestCustomViewState(isLoading2 = false), awaitItem())

                testViewModel.emitViewStateWithType<EmitTestCustomViewState> { EmitTestCustomViewState(isLoading2 = true) }
                assertEquals(EmitTestCustomViewState(isLoading2 = true), awaitItem())

                testViewModel.emitViewStateWithType<EmitTestCustomViewState> { EmitTestCustomViewState(isLoading2 = true) }
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
            testStatesModel.findViewStateStreamByType<EmitTestViewState>().getOrFail().test {
                assertEquals(EmitTestViewState(isLoading = false), awaitItem())

                testStatesModel.emitViewState { it.copy(isLoading = true) }
                assertEquals(EmitTestViewState(isLoading = true), awaitItem())

                testStatesModel.emitViewState { it.copy(isLoading = true) }
                expectNoEvents()
            }
        }

        @Test
        @DisplayName("Testing emit custom state")
        fun testEmitCustomState(): Unit = runTest {
            testStatesModel.registerCustomViewState(EmitTestCustomViewState(false))
            testStatesModel.findViewStateStreamByType<EmitTestCustomViewState>().getOrFail().test {
                assertEquals(EmitTestCustomViewState(isLoading2 = false), awaitItem())

                testStatesModel.emitViewStateWithType<EmitTestCustomViewState> { EmitTestCustomViewState(isLoading2 = true) }
                assertEquals(EmitTestCustomViewState(isLoading2 = true), awaitItem())

                testStatesModel.emitViewStateWithType<EmitTestCustomViewState> { EmitTestCustomViewState(isLoading2 = true) }
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
            testViewModel.findEventByType<EmitTestCustomEvent>().getOrFail().test {
                testViewModel.emitEvent(EmitTestCustomEvent(isLoading = true))
                assertEquals(EmitTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testViewModel.emitEvent(EmitTestCustomEvent(isLoading = true))
                assertEquals(EmitTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testViewModel.emitEvent(EmitTestCustomEvent(isLoading = false))
                assertEquals(EmitTestCustomEvent(isLoading = false), awaitItem().getContentIfNotHandled())
            }
        }
    }

    @Nested
    @DisplayName("Testing events on StatesModel")
    inner class EmitEventStatesModel {
        @Test
        @DisplayName("Testing emit event")
        fun testEmitViewState(): Unit = runTest {
            testStatesModel.findEventByType<EmitTestCustomEvent>().getOrFail().test {

                testStatesModel.emitEvent(EmitTestCustomEvent(isLoading = true))
                assertEquals(EmitTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testStatesModel.emitEvent(EmitTestCustomEvent(isLoading = true))
                assertEquals(EmitTestCustomEvent(isLoading = true), awaitItem().getContentIfNotHandled())

                testStatesModel.emitEvent(EmitTestCustomEvent(isLoading = false))
                assertEquals(EmitTestCustomEvent(isLoading = false), awaitItem().getContentIfNotHandled())
            }
        }
    }
}

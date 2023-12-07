@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package sk.alival.crutch.states

import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import sk.alival.crutch.states.logging.StatesLogger
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.streams.StatesEventStream
import sk.alival.crutch.states.streams.StatesStateStream
import sk.alival.crutch.states.streams.StatesStreamsContainer
import sk.alival.crutch.states.streams.registerCustomEvent
import sk.alival.crutch.states.streams.registerCustomViewState
import sk.alival.crutch.states.tests.StatesTestManager

internal data class StatesTestViewState(
    val isLoading: Boolean = false
)

internal data class StatesTestCustomViewState(
    val isLoading2: Boolean = false
)

internal data class StatesTestCustomEvent(
    val isLoading: Boolean = false
) : StatesOneTimeEvents

internal class StatesTestViewModel(defaultState: StatesTestViewState) : StatesViewModel<StatesTestViewState>(defaultState) {
    init {
        registerCustomEvent<StatesTestCustomEvent>()
    }
}

internal class StatesTestStatesModel(scope: CoroutineScope, defaultState: StatesTestViewState) : States<StatesTestViewState> {

    override val statesStreamsContainer: StatesStreamsContainer = StatesStreamsContainer(scope)

    init {
        registerCustomViewState(defaultState)
        registerCustomEvent<StatesTestCustomEvent>()
    }
}

abstract class BaseStatesTest {

    @BeforeEach
    fun setupStates() {
        val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
        Dispatchers.setMain(testDispatcher)
        StatesTestManager.isRunningInTests = true
        StatesLogger.isStatesDebugModeEnabled = AtomicBoolean(true)
    }

}

fun <T> StatesStateStream<T>?.getOrFail() = this?.stream ?: Assertions.fail("Stream not found")
fun <T : StatesOneTimeEvents> StatesEventStream<T>?.getOrFail() = this?.stream?.receiveAsFlow() ?: Assertions.fail("Stream not found")
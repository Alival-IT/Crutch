@file:OptIn(ExperimentalCoroutinesApi::class)

package sk.alival.crutch.states

import java.util.concurrent.atomic.AtomicBoolean
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
import sk.alival.crutch.states.tests.StatesTestManager

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
fun <T: StatesOneTimeEvents> StatesEventStream<T>?.getOrFail() = this?.stream?.receiveAsFlow() ?: Assertions.fail("Stream not found")
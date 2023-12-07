package sk.alival.crutch.states

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import sk.alival.crutch.states.endings.emitViewState
import sk.alival.crutch.states.tests.StatesTestManager
import sk.alival.crutch.states.tests.TestingEmitterCollector
import sk.alival.crutch.states.tests.expectStatesOrEvents

class EmitterCollectorTestExample : BaseStatesTest() {

    private val emitterCollector = TestingEmitterCollector()
    private val testViewModel by lazy { StatesTestViewModel(StatesTestViewState(isLoading = false)) }
    private val testStatesModel by lazy { StatesTestStatesModel(TestScope(), StatesTestViewState()) }

    @BeforeEach
    fun setupEmitterCollector() {
        StatesTestManager.isRunningInTests = true
        emitterCollector.reset()
        StatesTestManager.emitterCollector = emitterCollector
    }

    @Test
    fun emitterCollectorExampleViewModel() = runTest {
        testViewModel.emitViewState { it.copy(isLoading = true) }
        emitterCollector.expectStatesOrEvents(StatesTestViewState(isLoading = true), assertionFailedMessage = { fail(it) })
    }

    @Test
    fun emitterCollectorExampleStatesModel() = runTest {
        testStatesModel.emitViewState { it.copy(isLoading = true) }
        emitterCollector.expectStatesOrEvents(StatesTestViewState(isLoading = true), assertionFailedMessage = { fail(it) })
    }
}

package sk.alival.crutch.states

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import sk.alival.crutch.states.endings.emitViewState
import sk.alival.crutch.states.endings.emitViewStateWithType
import sk.alival.crutch.states.stateHelpers.getLastState
import sk.alival.crutch.states.streams.registerCustomViewState

class StatesLastStateTests : BaseStatesTest() {

    private val testViewModel by lazy { StatesTestViewModel(StatesTestViewState(isLoading = false)) }
    private val testStatesModel by lazy { StatesTestStatesModel(TestScope(), StatesTestViewState()) }

    @Test
    @DisplayName("Testing getLastState for defaultState with viewModel")
    fun testGetLastStateForViewModel(): Unit = runTest {
        Assertions.assertEquals(StatesTestViewState(isLoading = false), testViewModel.getLastState<StatesTestViewState>())
        testViewModel.emitViewState { it.copy(isLoading = true) }
        Assertions.assertEquals(StatesTestViewState(isLoading = true), testViewModel.getLastState<StatesTestViewState>())
    }

    @Test
    @DisplayName("Testing getLastState for defaultState stateModel")
    fun testGetLastStateForStateModel(): Unit = runTest {
        Assertions.assertEquals(StatesTestViewState(isLoading = false), testStatesModel.getLastState<StatesTestViewState>())
        testStatesModel.emitViewState { it.copy(isLoading = true) }
        Assertions.assertEquals(StatesTestViewState(isLoading = true), testStatesModel.getLastState<StatesTestViewState>())
    }

    @Test
    @DisplayName("Testing getLastState for custom state with viewModel")
    fun testGetLastCustomStateForViewModel(): Unit = runTest {
        testViewModel.registerCustomViewState(StatesTestCustomViewState(false))
        Assertions.assertEquals(StatesTestCustomViewState(isLoading2 = false), testViewModel.getLastState<StatesTestCustomViewState>())
        testViewModel.emitViewStateWithType<StatesTestCustomViewState> { it.copy(isLoading2 = true) }
        Assertions.assertEquals(StatesTestCustomViewState(isLoading2 = true), testViewModel.getLastState<StatesTestCustomViewState>())
    }

    @Test
    @DisplayName("Testing getLastState for custom state stateModel")
    fun testGetLastCustomStateForStateModel(): Unit = runTest {
        testStatesModel.registerCustomViewState(StatesTestCustomViewState(false))
        Assertions.assertEquals(StatesTestCustomViewState(isLoading2 = false), testStatesModel.getLastState<StatesTestCustomViewState>())
        testStatesModel.emitViewStateWithType<StatesTestCustomViewState> { it.copy(isLoading2 = true) }
        Assertions.assertEquals(StatesTestCustomViewState(isLoading2 = true), testStatesModel.getLastState<StatesTestCustomViewState>())
    }
}

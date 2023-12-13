package sk.alival.crutch.states

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sk.alival.crutch.states.savedStateHandleManager.SavedStateHandleManagerImpl
import sk.alival.crutch.states.streams.StatesStreamsContainer
import sk.alival.crutch.states.streams.registerCustomViewState

/**
 * States view model with a more convenient usage for viewModels.
 * It automatically registers the initial viewState and provides coroutineScope
 *
 * @param VIEWSTATE type of default state
 * @property initialState initial state that is created and registered
 */
abstract class StatesViewModel<VIEWSTATE : Any>(
    private val initialState: VIEWSTATE,
    savedStateHandle: SavedStateHandle? = null,
    initialStateSavedStateHandleKey: String? = null
) : ViewModel(), States<VIEWSTATE> {

    final override val statesStreamsContainer: StatesStreamsContainer = StatesStreamsContainer(viewModelScope, savedStateHandle?.let { SavedStateHandleManagerImpl(it) })

    init {
        registerCustomViewState(initialState, initialStateSavedStateHandleKey)
    }
}

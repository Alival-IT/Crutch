package sk.alival.crutch.states

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import sk.alival.crutch.states.streams.registerCustomViewState

/**
 * States view model with a more convenient usage for viewModels.
 * It automatically registers the initial viewState and provides coroutineScope
 *
 * @param VIEWSTATE type of default state
 * @property initialState initial state that is created and registered
 */
abstract class StatesViewModel<VIEWSTATE : Any>(
    private val initialState: VIEWSTATE
) : ViewModel(), States<VIEWSTATE> {

    override fun getInitialViewState(): VIEWSTATE = initialState

    override fun getCoroutineScope(): CoroutineScope = viewModelScope

    /**
     * Automatically registers the initialState
     */
    override fun registerStates() {
        registerCustomViewState(getInitialViewState())
    }
}

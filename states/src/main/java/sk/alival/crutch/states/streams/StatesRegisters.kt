package sk.alival.crutch.states.streams

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import sk.alival.crutch.logging.getNameForLogs
import sk.alival.crutch.states.States
import sk.alival.crutch.states.logging.StatesLogger
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.savedStateHandleManager.SavedStateHandleManager

/**
 * Register custom view state
 *
 * @param initialData to initialize the stream with
 */
fun States<*>.registerCustomViewState(initialData: Any, savedStateHandleKey: String? = null) {
    StatesLogger.logM { "Registering data flow for type: ${initialData::class.java.getNameForLogs()}, in: ${this::class.java.getNameForLogs()}, with savedStateHandleKey $savedStateHandleKey" }
    if (statesStreamsContainer.savedStateHandleManager != null && savedStateHandleKey == null) {
        StatesLogger.logM { "We encourage you to set your own key for savedStateHandle." }
    }
    statesStreamsContainer.dataFlows[initialData::class] = StatesStateStream(
        MutableStateFlow(
            statesStreamsContainer.savedStateHandleManager
                ?.getValue(SavedStateHandleManager.createKey(this::class, savedStateHandleKey)) ?: initialData
        ),
        Mutex(),
        SavedStateHandleManager.createKey(this::class, savedStateHandleKey)
    )
}

/**
 * Register custom event
 *
 * @param T type of the events
 */
inline fun <reified T : StatesOneTimeEvents> States<*>.registerCustomEvent() {
    StatesLogger.logM { "Registering one time event for type: ${T::class.java.getNameForLogs()} in  ${this::class.java.getNameForLogs()}" }
    statesStreamsContainer.oneTimeEvents[T::class] = StatesEventStream(Channel(capacity = 1), Mutex())
}

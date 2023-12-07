package sk.alival.crutch.states.streams

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import sk.alival.crutch.states.States
import sk.alival.crutch.states.logging.StatesLogger
import sk.alival.crutch.logging.getNameForLogs
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents

/**
 * Register custom view state
 *
 * @param initialData to initialize the stream with
 */
fun States<*>.registerCustomViewState(initialData: Any) {
    StatesLogger.log { "Registering data flow for type: ${initialData::class.java.getNameForLogs()}, in: ${this::class.java.getNameForLogs()}" }
    statesStreamsContainer.dataFlows[initialData::class] = StatesStateStream(MutableStateFlow(initialData), Mutex())
}

/**
 * Register custom event
 *
 * @param T type of the events
 */
inline fun <reified T : StatesOneTimeEvents> States<*>.registerCustomEvent() {
    StatesLogger.log { "Registering one time event for type: ${T::class.java.getNameForLogs()} in  ${this::class.java.getNameForLogs()}" }
    statesStreamsContainer.oneTimeEvents[T::class] = StatesEventStream(Channel(capacity = 1), Mutex())
}

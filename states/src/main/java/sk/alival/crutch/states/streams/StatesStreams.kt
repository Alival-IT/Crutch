package sk.alival.crutch.states.streams

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEventsWrapper

/**
 * State stream
 *
 */
data class StatesStateStream<T>(
    val stream: MutableStateFlow<T>,
    val mutex: Mutex,
    val savedStateHandleKey: String
)

/**
 * Event stream
 *
 */
data class StatesEventStream<T : StatesOneTimeEvents>(
    val stream: Channel<StatesOneTimeEventsWrapper<T>>,
    val mutex: Mutex
)

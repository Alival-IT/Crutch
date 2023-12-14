@file:SuppressLint("ComposableNaming")

package sk.alival.crutch.states.endings

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import sk.alival.crutch.kover.KoverIgnore
import sk.alival.crutch.states.States
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.streams.findEventByType
import sk.alival.crutch.states.streams.findViewStateStreamByType

/**
 * Observe events
 *
 * @param T type of events
 * @param onEvent invoked when new event if received
 */
@KoverIgnore
@Composable
inline fun <reified T : StatesOneTimeEvents> States<*>.observeEvents(crossinline onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = this) {
        launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                findEventByType<T>()?.stream?.receiveAsFlow()?.collect { event ->
                    event.getContentIfNotHandled()?.let { onEvent(it) }
                }
            }
        }
    }
}

/**
 * Observe view state for default state of [States]
 *
 * @param T type of events
 * @return value of last emitted state
 */
@KoverIgnore
@Composable
inline fun <reified T : Any> States<T>.observeViewState(): T {
    return this.observeViewStateAsState().value
}

/**
 * Observe view state as state for default state of [States]
 *
 * @param T type of events
 * @return [State] of last emitted value
 */
@KoverIgnore
@Composable
inline fun <reified T : Any> States<T>.observeViewStateAsState(): State<T> {
    return (this.findViewStateStreamByType<T>()?.stream ?: MutableStateFlow(null).filterNotNull() as MutableStateFlow)
        .let { it.collectAsStateWithLifecycle(it.value) }
}

/**
 * Observe view state with type
 *
 * @param T type of events
 * @return value of last emitted state
 */
@KoverIgnore
@Composable
inline fun <reified T : Any> States<*>.observeViewStateWithType(): T {
    return (this.findViewStateStreamByType<T>()?.stream ?: MutableStateFlow(null).filterNotNull() as MutableStateFlow)
        .let { it.collectAsStateWithLifecycle(it.value).value }
}

/**
 * Observe view state as state for custom state of [States]
 *
 * @param T type of events
 * @return [State] of last emitted value
 */
@KoverIgnore
@Composable
inline fun <reified T : Any> States<*>.observeViewStateAsStateWithType(): State<T> {
    return (this.findViewStateStreamByType<T>()?.stream ?: MutableStateFlow(null).filterNotNull() as MutableStateFlow)
        .collectAsStateWithLifecycle()
}

package sk.alival.crutch.states.stateHelpers

import sk.alival.crutch.states.States
import sk.alival.crutch.states.logging.StatesLogger
import sk.alival.crutch.logging.getNameForLogs
import sk.alival.crutch.states.streams.findViewStateStreamByType

/**
 * Get last state by type
 *
 * @param T type of the state we are searching for
 * @return state if found
 */
inline fun <reified T : Any> States<*>.getLastState(): T? {
    return findViewStateStreamByType<T>()?.stream?.value.also {
        if (it == null) {
            StatesLogger.log { "Last state not found for ${T::class.getNameForLogs()}" }
        } else {
            StatesLogger.log { "Last state found: $it" }
        }
    }
}

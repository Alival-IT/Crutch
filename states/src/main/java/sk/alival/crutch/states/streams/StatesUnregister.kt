package sk.alival.crutch.states.streams

import sk.alival.crutch.logging.getNameForLogs
import sk.alival.crutch.states.States
import sk.alival.crutch.states.logging.StatesLogger

/**
 * Unregister custom view state for type [T]
 *
 */
inline fun <reified T> States<*>.unRegisterCustomViewState() {
    StatesLogger.logM { "UnRegistering data flow for type: ${T::class.java.getNameForLogs()}, in: ${this::class.java.getNameForLogs()}" }
    statesStreamsContainer.dataFlows.remove(T::class)
}

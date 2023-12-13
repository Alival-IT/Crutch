@file:Suppress("UNCHECKED_CAST")

package sk.alival.crutch.states.streams

import sk.alival.crutch.states.States
import sk.alival.crutch.states.logging.StatesLogger
import sk.alival.crutch.logging.getNameForLogs
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents

/**
 * Find view state stream by type
 *
 * @param T type of stream we are searching for
 * @return [StatesStateStream] if found, have to be registered first
 */
inline fun <reified T : Any> States<*>.findViewStateStreamByType(): StatesStateStream<T>? {
    return (
            this.statesStreamsContainer.dataFlows.toList()
                .firstOrNull {
                    it.first.java.isAssignableFrom(T::class.java) || it.first == T::class
                }?.second as? StatesStateStream<T>?
            )
        .also { stream ->
            if (stream == null) {
                StatesLogger.logM { "ViewStateStream for type ${T::class.java.getNameForLogs()} not found\nRegistered: ${this.statesStreamsContainer.dataFlows.mapNotNull { it.key.getNameForLogs() }.joinToString(separator = "\n")}" }
            } else {
                StatesLogger.logM { "ViewStateStream found for type ${T::class.java.getNameForLogs()}" }
            }
        }
}

/**
 * Find event stream by type
 *
 * @param T type of stream we are searching for
 * @return [StatesEventStream] if found, have to be registered first
 */
inline fun <reified T : StatesOneTimeEvents> States<*>.findEventByType(): StatesEventStream<T>? {
    return (
            this.statesStreamsContainer.oneTimeEvents.toList()
                .firstOrNull {
                    it.first.java.isAssignableFrom(T::class.java) || it.first == T::class
                }?.second as? StatesEventStream<T>?
            )
        .also { stream ->
            if (stream == null) {
                StatesLogger.logM { "EventStream for type ${T::class.java.getNameForLogs()} not found\nRegistered: ${this.statesStreamsContainer.oneTimeEvents.mapNotNull { it.key.getNameForLogs() }.joinToString(separator = "\n")}" }
            } else {
                StatesLogger.logM { "EventStream found for type ${T::class.java.getNameForLogs()}" }
            }
        }
}

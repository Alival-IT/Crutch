package sk.alival.crutch.states.endings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.withLock
import sk.alival.crutch.states.States
import sk.alival.crutch.states.coroutineHelpers.withMain
import sk.alival.crutch.states.coroutineHelpers.withMainImmediate
import sk.alival.crutch.states.logging.StatesLogger
import sk.alival.crutch.logging.getNameForLogs
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEventsWrapper
import sk.alival.crutch.states.streams.findEventByType
import sk.alival.crutch.states.streams.findViewStateStreamByType
import sk.alival.crutch.states.tests.StatesTestManager

/**
 * Emit default view state of [States]
 *
 * @param T type of state
 * @param newState that will me emitted
 * @receiver last state
 */
suspend inline fun <reified T : Any> States<T>.emitViewState(crossinline newState: (T) -> T) {
    emitViewStateWithType<T>(newState)
}

/**
 * Emit view state with type
 *
 * @param T type of state
 * @param newState that will me emitted
 * @receiver last state
 */
suspend inline fun <reified T : Any> States<*>.emitViewStateWithType(crossinline newState: (T) -> T) {
    findViewStateStreamByType<T>()?.also { stream ->
        stream.mutex.withLock {
            stream.stream.value.let { previousState ->
                val block: suspend CoroutineScope.() -> Unit = {
                    val nextState = newState(previousState).also { new ->
                        StatesLogger.log { "Previous state: $previousState \nNew state:$new ->" }
                    }
                    stream.stream.emit(nextState)
                    StatesTestManager.emitterCollector?.collect(nextState)
                    StatesLogger.log {
                        """
                    Thread info:
                        Is active: ${this.isActive}
                        Thread name: ${Thread.currentThread().name}
                    Stream info:
                        Subscription count: ${stream.stream.subscriptionCount.value}
                        """.trimIndent()
                    }
                }
                if (StatesTestManager.isRunningInTests) {
                    withMain(block = block)
                } else {
                    withMainImmediate(block = block)
                }
            }
        }
    } ?: StatesLogger.log { "Skipping emitted state, stream type not found for ${T::class.java.getNameForLogs()}:\nregistered:${statesStreamsContainer.dataFlows}\n${T::class.getNameForLogs()}}" }
}

/**
 * Emit event
 *
 * @param T type of event
 * @param event event
 */
suspend inline fun <reified T : StatesOneTimeEvents> States<*>.emitEvent(event: T) {
    findEventByType<T>()?.also { stream ->
        stream.mutex.withLock {
            val block: suspend CoroutineScope.() -> Unit = {
                stream.stream.send(StatesOneTimeEventsWrapper(event))
                StatesTestManager.emitterCollector?.collect(event)
                StatesLogger.log { "Posting one time event: $event" }
                StatesLogger.log {
                    """
                    Thread info:
                        Is active: ${this.isActive}
                        Thread name: ${Thread.currentThread().name}
                        """.trimIndent()
                }
            }
            if (StatesTestManager.isRunningInTests) {
                withMain(block = block)
            } else {
                withMainImmediate(block = block)
            }
        }
    } ?: StatesLogger.log { "Skipping one time event, stream type not found fir type ${T::class.java.getNameForLogs()}:\nregistered:${statesStreamsContainer.oneTimeEvents}\nemitting:${T::class.getNameForLogs()}" }
}

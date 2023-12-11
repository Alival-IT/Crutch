package sk.alival.crutch.states.streams

import kotlinx.coroutines.CoroutineScope
import sk.alival.crutch.states.States
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import kotlin.reflect.KClass

class StatesStreamsContainer(
    coroutineScope: CoroutineScope,
) {
    val scope: CoroutineScope = coroutineScope

    /**
     * Map for your registered states since [States] supports multiple states
     */
    val dataFlows: MutableMap<KClass<*>, StatesStateStream<Any>> = mutableMapOf()

    /**
     * Map for your registered events since [States] supports multiple events
     */
    val oneTimeEvents: MutableMap<KClass<*>, StatesEventStream<StatesOneTimeEvents>> = mutableMapOf()
}

package sk.alival.crutch.states.streams

import kotlinx.coroutines.CoroutineScope
import sk.alival.crutch.states.States
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.savedStateHandleManager.SavedStateHandleManager
import kotlin.reflect.KClass

/**
 * States streams container
 *
 * @property scope on which States operates on
 * @property savedStateHandleManager for savedStateFunctionality, you can pass an instance of [sk.alival.crutch.states.savedStateHandleManager.SavedStateHandleManagerImpl]
 * @constructor Create empty States streams container
 */
class StatesStreamsContainer(
    val scope: CoroutineScope,
    val savedStateHandleManager: SavedStateHandleManager? = null
) {
    /**
     * Map for your registered states since [States] supports multiple states
     */
    val dataFlows: MutableMap<KClass<*>, StatesStateStream<Any>> = mutableMapOf()

    /**
     * Map for your registered events since [States] supports multiple events
     */
    val oneTimeEvents: MutableMap<KClass<*>, StatesEventStream<StatesOneTimeEvents>> = mutableMapOf()
}

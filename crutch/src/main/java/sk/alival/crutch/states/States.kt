package sk.alival.crutch.states

import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEvents
import sk.alival.crutch.states.streams.StatesEventStream
import sk.alival.crutch.states.streams.StatesStateStream
import kotlin.reflect.KClass

/**
 * [States] interface holding your states and events.
 *
 * States are supporting multiple viewStates and events.
 *
 * Also providing helper methods to find the stream, emit, observe and so on.
 *
 * @param VIEWSTATE type of your main viewState for [States]
 */
interface States<VIEWSTATE : Any> {
    /**
     * Map for your registered states since [States] supports multiple states
     */
    val dataFlows: MutableMap<KClass<*>, StatesStateStream<Any>>
        get() = ConcurrentHashMap()

    /**
     * Map for your registered events since [States] supports multiple events
     */
    val oneTimeEvents: MutableMap<KClass<*>, StatesEventStream<StatesOneTimeEvents>>
        get() = ConcurrentHashMap()

    /**
     * Coroutine scope [States] operates on
     *
     * @return [CoroutineScope] which is used for operations on streams
     */
    fun getCoroutineScope(): CoroutineScope

    /**
     * Get initial view state
     *
     * @return initial view state
     */
    fun getInitialViewState(): VIEWSTATE

    /**
     * Registration of default viewState.
     *
     */
    fun registerStates()
}

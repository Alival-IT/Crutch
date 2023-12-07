package sk.alival.crutch.states

import sk.alival.crutch.states.streams.StatesStreamsContainer

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
     * States streams container holding streams for viewStates and events and scope on which States operates
     */
    val statesStreamsContainer: StatesStreamsContainer
}

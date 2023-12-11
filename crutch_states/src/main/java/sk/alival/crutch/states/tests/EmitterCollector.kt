package sk.alival.crutch.states.tests

/**
 * Emitter collector used for testing purposes to collect all the events and viewStates
 *
 * @constructor Create empty Emitter collector
 */
interface EmitterCollector {
    /**
     * Collecting states and events
     *
     */
    fun collect(stateOrEvent: Any)
}

package sk.alival.crutch.states.tests

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import sk.alival.crutch.states.States
import sk.alival.crutch.states.coroutineHelpers.launchMain

/**
 * States test helper
 *
 * @constructor Create empty States test manager
 */
object StatesTestManager {
    /**
     * When running in tests, should be set to true
     */
    var isRunningInTests = false

    /**
     * Emitter collector collecting all events and viewStates if set.
     * Usually used for testing.
     */
    var emitterCollector: EmitterCollector? = null

    /**
     * Method to change the scope if we are in testing mode, used in StatesLaunchers
     *
     */
    fun States<*>.wrapForTest(job: suspend (CoroutineScope) -> Unit, originalJob: Job): Job {
        return if (isRunningInTests) {
            getCoroutineScope().launchMain {
                job(this)
            }
        } else {
            originalJob
        }
    }
}

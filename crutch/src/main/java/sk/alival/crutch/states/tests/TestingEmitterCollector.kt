package sk.alival.crutch.states.tests

import androidx.annotation.NonNull
import java.util.Collections
import sk.alival.crutch.states.logging.StatesLogger

/**
 * Basic implementation of testing emitter collector
 *
 */
class TestingEmitterCollector : EmitterCollector {
    private val emittedStatesAndEvents: MutableList<Any> = Collections.synchronizedList(mutableListOf())

    /**
     * Remove all previously collected states and events
     */
    fun reset() {
        StatesLogger.log { "Resetting EmitterCollector $this" }
        emittedStatesAndEvents.clear()
    }

    override fun collect(stateOrEvent: Any) {
        StatesLogger.log { "Collecting state for EmitterCollector $this: $stateOrEvent" }
        println("Collecting state for EmitterCollector $this: $stateOrEvent")
        emittedStatesAndEvents.add(stateOrEvent)
    }

    /**
     * Verify that the given state or event [expected] happened in the past, order of states does NOT matter in this case
     *
     * @param T type of the state
     * @param expected state
     * @param comparator can create a custom comparing function of 2 states if the regular == is not enough
     * @receiver
     */
    fun <T> assertStateOrEvent(@NonNull expected: T, assertionFailedMessage: (String) -> Unit, comparator: (T, T) -> Boolean = { a, b -> a == b }) {
        val expectedValue = emittedStatesAndEvents.filterIsInstance(expected!!::class.java)
            .firstOrNull {
                comparator(it, expected)
            }

        if (expectedValue == null) {
            assertionFailedMessage(
                "Expected state not found\nExpected state:\n${expected}\nPrevious states:\n${
                    emittedStatesAndEvents.reversed().joinToString(
                        separator = "\n"
                    )
                }"
            )
        } else {
            emittedStatesAndEvents.filterIsInstance(expected!!::class.java).dropWhile { item -> !comparator(item, expected) }
            emittedStatesAndEvents.remove(expectedValue)
        }
    }

    /**
     * Verify that the given [expected] state was emitted as the LAST state of viewModel
     *
     * @param T type of the state
     * @param expected state
     * @param comparator can create a custom comparing function of 2 states if the regular == is not enought
     * @receiver
     */
    fun <T> assertLatestStateOrEvent(@NonNull expected: T, assertionFailedMessage: (String) -> Unit, comparator: (T, T) -> Boolean = { a, b -> a == b }) {
        val last = emittedStatesAndEvents.filterIsInstance(expected!!::class.java).lastOrNull()
        val expectedValue = last?.let {
            if (comparator(it, expected)) {
                expected
            } else {
                null
            }
        }

        if (expectedValue == null) {
            assertionFailedMessage(
                "Expected state not found\nExpected state:\n${expected}\n LatestState:\n${last}\n older states\n${
                    emittedStatesAndEvents.reversed().joinToString(
                        separator = "\n"
                    )
                }"
            )
        } else {
            emittedStatesAndEvents.clear()
        }
    }
}

/**
 * Expect states or events that were collected by EmitterCollector in any time, check [TestingEmitterCollector.assertStateOrEvent]
 *
 * @param STATE_TYPE - type of the state class
 * @param expected states vararg
 * @see [TestingEmitterCollector.assertStateOrEvent]
 */
@Synchronized
fun <STATE_TYPE> TestingEmitterCollector.expectStatesOrEvents(@NonNull vararg expected: STATE_TYPE, assertionFailedMessage: (String) -> Unit, comparator: (STATE_TYPE, STATE_TYPE) -> Boolean = { a, b -> a == b }) {
    expected.forEach {
        (StatesTestManager.emitterCollector as? TestingEmitterCollector?)?.assertStateOrEvent(it, assertionFailedMessage, comparator = comparator)
            ?: error("Emitter not set")
    }
}

/**
 * Expect latest state or event that was collected by EmitterCollector, check [TestingEmitterCollector.assertLatestStateOrEvent]
 *
 * @param STATE_TYPE - type of the state class
 * @param expected state
 * @see [TestingEmitterCollector.assertLatestStateOrEvent]
 */
@Synchronized
fun <STATE_TYPE> TestingEmitterCollector.expectLatestStateOrEvent(@NonNull expected: STATE_TYPE, assertionFailedMessage: (String) -> Unit, comparator: (STATE_TYPE, STATE_TYPE) -> Boolean = { a, b -> a == b }) {
    (StatesTestManager.emitterCollector as? TestingEmitterCollector?)?.assertLatestStateOrEvent(expected, assertionFailedMessage, comparator = comparator)
        ?: error("Emitter not set")
}

package sk.alival.crutch.states.onetimeEvents

import java.util.concurrent.atomic.AtomicBoolean

/**
 * One time events wrapper
 *
 * @param T type of event
 * @property content event
 */
open class StatesOneTimeEventsWrapper<out T : StatesOneTimeEvents>(private val content: T) {

    private var hasBeenHandled = AtomicBoolean(false)

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled.compareAndSet(true, false)) {
            null
        } else {
            hasBeenHandled.set(true)
            content
        }
    }
}

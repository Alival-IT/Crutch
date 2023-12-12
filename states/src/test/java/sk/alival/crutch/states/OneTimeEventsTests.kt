package sk.alival.crutch.states

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import sk.alival.crutch.states.onetimeEvents.StatesOneTimeEventsWrapper

class OneTimeEventsTests : BaseStatesTest() {
    @Test
    fun testOneTimeEvents() {
        val event = StatesOneTimeEventsWrapper(StatesTestCustomEvent(true))
        Assertions.assertNotNull(event.getContentIfNotHandled())
        Assertions.assertNull(event.getContentIfNotHandled())
    }
}

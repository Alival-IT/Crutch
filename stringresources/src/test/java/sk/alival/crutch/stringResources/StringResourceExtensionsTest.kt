package sk.alival.crutch.stringResources

import android.content.Context
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StringResourceExtensionsTest {

    @Test
    fun testToStringResourceWithString() {
        val resourceString = "testString"
        val actualResource = resourceString.toStringResource()

        assertTrue(StringResource.StringValueResource(resourceString) == actualResource)
    }

    @Test
    fun testToStringResourceWithNullString() {
        val actualResource = null.toStringResource()

        assertEquals(StringResource.StringValueResource(null), actualResource)
    }

    @Test
    fun testToStringResourceWithResourceId() {
        val resourceId = 123
        val actualResource = resourceId.toStringResource()
        val expected = StringResource.StringIdResource(resourceId)

        assertTrue(expected == actualResource)
    }

    @Test
    fun testIsEmptyWithNullResource() {
        val actualIsEmpty = null.isEmpty(mockk<Context>())

        assertTrue(actualIsEmpty)
    }

    @Test
    fun testIsEmptyWithEmptyString() {
        val resource = StringResource.StringValueResource("")
        val actualIsEmpty = resource.isEmpty(mockk<Context>())

        assertTrue(actualIsEmpty)
    }

    @Test
    fun testIsEmptyWithNonEmptyString() {
        val resource = StringResource.StringValueResource("test")
        val actualIsEmpty = resource.isEmpty(mockk<Context>())

        assertFalse(actualIsEmpty)
    }
}

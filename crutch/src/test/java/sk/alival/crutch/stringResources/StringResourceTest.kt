package sk.alival.crutch.stringResources

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StringResourceTest {

    @Test
    @DisplayName("Test orEmpty")
    fun orEmptyTest() {
        val resNull: StringResource? = null
        assertEquals(resNull.orEmpty(), StringResource.EmptyStringResource())

        val resNonNull: StringResource = StringResource.StringValueResource("a")
        assertEquals(resNonNull.orEmpty(), resNonNull)
    }

    @Nested
    @DisplayName("StringValueResourceTests")
    inner class StringValueResourceTests {

        @Test
        @DisplayName("Testing Equals")
        fun testStringResValue() {
            assertEquals(StringResource.StringValueResource(null), StringResource.StringValueResource(null))
            assertEquals(StringResource.StringValueResource(null, "abc"), StringResource.StringValueResource(null, "abc"))
            assertNotEquals(StringResource.StringValueResource(null, "abc"), StringResource.StringValueResource(null, "abc", "def"))
            assertNotEquals(StringResource.StringValueResource("abc"), StringResource.StringValueResource("def"))

            assertEquals(StringResource.StringValueResource("DeF"), StringResource.StringValueResource("DeF"))
            assertEquals(StringResource.StringValueResource("DeF", "abc"), StringResource.StringValueResource("DeF", "abc"))
            assertNotEquals(StringResource.StringValueResource("DeF", "abc"), StringResource.StringValueResource("DeF", "abc", "def"))
            assertNotEquals(StringResource.StringValueResource("AbC", "abc"), StringResource.StringValueResource("DeF", "abc"))

        }

        @Test
        @DisplayName("Testing toString")
        fun testToString() {
            assertTrue(StringResource.StringValueResource(null).toString().isNotBlank())
        }

        @Test
        @DisplayName("Testing toHash")
        fun testHash() {
            assertTrue(StringResource.StringValueResource(null).hashCode() != 0)
        }

        @Test
        @DisplayName("Testing getString")
        fun testGetString() {
            assertEquals(StringResource.StringValueResource("abc").getString(null), "abc")
            assertEquals(StringResource.StringValueResource("abc %s", "def").getString(null), "abc def")
            assertEquals(StringResource.StringValueResource("abc %s").getString(null), "abc %s")
            assertEquals(StringResource.StringValueResource("abc", "def").getString(null), "abc")
            assertEquals(StringResource.StringValueResource("").getString(null), "")
            assertEquals(StringResource.StringValueResource(null).getString(null), null)
            assertEquals(StringResource.StringValueResource(null, "def").getString(null), null)
        }
    }

    @Nested
    @DisplayName("StringIdResourceTests")
    inner class StringIdResourceTests {
        @Test
        @DisplayName("Testing Equals")
        fun testStringResValue() {
            assertEquals(StringResource.StringIdResource(0), StringResource.StringIdResource(0))
            assertEquals(StringResource.StringIdResource(0, "abc", "def"), StringResource.StringIdResource(0, "abc", "def"))
            assertNotEquals(StringResource.StringIdResource(0, "abc", "def"), StringResource.StringIdResource(0, "abc"))
        }

        @Test
        @DisplayName("Testing toString")
        fun testToString() {
            assertTrue(StringResource.StringIdResource(0).toString().isNotBlank())
        }

        @Test
        @DisplayName("Testing toHash")
        fun testHash() {
            assertTrue(StringResource.StringIdResource(0).hashCode() != 0)
        }

        @Test
        @DisplayName("Testing getString")
        fun testGetString() {
            val mockedContext: Context = mockContextGetString("abc")
            assertEquals(StringResource.StringIdResource(0).getString(mockedContext), "abc")
        }
    }

    @Nested
    @DisplayName("StringAnnotatedStringResourceTests")
    inner class StringAnnotatedStringResourceTests {
        @Test
        @DisplayName("Testing Equals")
        fun testStringResValue() {
            val textString1 = AnnotatedString("Hello world", spanStyle = SpanStyle(color = Color.Blue), paragraphStyle = ParagraphStyle(textAlign = TextAlign.End))
            val textString2 = AnnotatedString("Hello world", spanStyle = SpanStyle(color = Color.Blue), paragraphStyle = ParagraphStyle(textAlign = TextAlign.End))

            val textString3 = AnnotatedString("Hello world", spanStyle = SpanStyle(color = Color.Blue), paragraphStyle = ParagraphStyle(textAlign = TextAlign.End))
            val textString4 = AnnotatedString("Hello world", spanStyle = SpanStyle(color = Color.Red), paragraphStyle = ParagraphStyle(textAlign = TextAlign.End))

            assertEquals(StringResource.StringAnnotatedStringResource(AnnotatedString("Hello world")), StringResource.StringAnnotatedStringResource(AnnotatedString("Hello world")))
            assertEquals(textString1, textString2)
            assertNotEquals(textString3, textString4)
        }

        @Test
        @DisplayName("Testing toString")
        fun testToString() {
            assertTrue(StringResource.StringAnnotatedStringResource(AnnotatedString("Hello world")).toString().isNotBlank())
        }

        @Test
        @DisplayName("Testing toHash")
        fun testHash() {
            assertTrue(StringResource.StringAnnotatedStringResource(AnnotatedString("Hello world")).hashCode() != 0)
        }

        @Test
        @DisplayName("Testing getString")
        fun testGetString() {
            assertEquals(StringResource.StringAnnotatedStringResource(AnnotatedString("Hello world")).getString(null), "Hello world")
            assertEquals(StringResource.StringAnnotatedStringResource(AnnotatedString("Hello world")).getAnnotatedString(null), AnnotatedString("Hello world"))
        }
    }

    @Nested
    @DisplayName("EmptyStringResourceTests")
    inner class EmptyStringResourceTests {
        @Test
        @DisplayName("Testing getString")
        fun testGetString() {
            assertEquals(StringResource.EmptyStringResource(), StringResource.EmptyStringResource())
            assertEquals(StringResource.EmptyStringResource().getString(null), StringResource.EmptyStringResource().getString(null))
            assertEquals(null, StringResource.EmptyStringResource().getString(null))
        }
    }

    private fun mockContextGetString(returnValue: String): Context {
        return mockk(relaxed = true) {
            every { getString(any()) } answers { returnValue }
            every { getString(any(), any()) } answers { returnValue }
            every { getString(any(), any(), any()) } answers { returnValue }
            every { getString(any(), any(), any(), any()) } answers { returnValue }
            every { getString(any(), any(), any(), any(), any()) } answers { returnValue }
        }
    }
}

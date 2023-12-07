package sk.alival.crutch.stringResources

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import java.io.Serializable

/**
 * String resource to unify passing string values or ids with safe params
 */
@Immutable
sealed class StringResource : Serializable {

    companion object {
        private const val serialVersionUID: Long = 835716347652
    }

    /**
     * String id resource
     *
     * @property stringId resource
     * @property params to fill into resource placeholders
     * @constructor Create StringIdResource with StringRes and optional params
     */
    @Immutable
    class StringIdResource(@StringRes val stringId: Int, vararg val params: Any) : StringResource() {
        override fun hashCode(): Int {
            var result = stringId
            result = 31 * result + params.contentHashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringIdResource

            if (stringId != other.stringId) return false
            if (!params.contentEquals(other.params)) return false

            return true
        }

        override fun toString(): String {
            return "StringIdResource(messageResId=${(stringId)}, params=${params.contentToString()})"
        }
    }

    /**
     * String value resource
     *
     * @property string
     * @property params to fill into value placeholders
     * @constructor Create StringValueResource with value and optional params
     */
    @Immutable
    open class StringValueResource(
        val string: String?,
        vararg val params: Any
    ) : StringResource() {

        override fun toString(): String {
            return "StringValueResource(string=${(string)}, params=${params.contentToString()})"
        }

        override fun hashCode(): Int {
            var result = string.hashCode()
            result = 31 * result + params.contentHashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringValueResource

            if (string != other.string) return false
            if (!params.contentEquals(other.params)) return false

            return true
        }
    }

    @Immutable
    class EmptyStringResource : StringValueResource(null)

    @Immutable
    class StringAnnotatedStringResource(
        val annotatedString: AnnotatedString
    ) : StringResource() {

        override fun toString(): String {
            return "StringValueResource(StringAnnotatedStringResource=${(annotatedString)})"
        }

        override fun hashCode(): Int {
            return annotatedString.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringAnnotatedStringResource

            if (annotatedString != other.annotatedString) return false

            return true
        }
    }

    /**
     * Get string
     *
     * @param context required for getting string by Id
     * @return string
     */
    fun getString(context: Context?): String? {
        StringResourcesLogger.logM { "Creating string from $this" }
        return when (this) {
            is StringIdResource -> getStringIdResourceString(this, context)
            is StringValueResource -> getStringValueResourceString(this, context)
            is StringAnnotatedStringResource -> getStringAnnotatedStringResourceString(this)
        }
    }

    /**
     * Get annotated string
     *
     * @param context required for getting string by Id
     * @return annotatedString
     */
    fun getAnnotatedString(context: Context?): AnnotatedString? {
        StringResourcesLogger.logM { "Creating annotated string from $this" }
        return when (this) {
            is StringAnnotatedStringResource -> this.annotatedString
            is StringIdResource -> getString(context)?.let { AnnotatedString(it) }
            is StringValueResource -> getString(context)?.let { AnnotatedString(it) }
        }
    }

    private fun getStringAnnotatedStringResourceString(stringAnnotatedStringResource: StringAnnotatedStringResource): String? {
        return try {
            stringAnnotatedStringResource.annotatedString.text
        } catch (t: Throwable) {
            StringResourcesLogger.logT { t }
            null
        }
    }

    private fun getStringValueResourceString(stringValueResource: StringValueResource, context: Context?): String? {
        return stringValueResource.string?.let {
            val fixedParams = stringValueResource.params
                .map {
                    if (it is StringResource) it.getString(context).orEmpty() else it
                }.toTypedArray()

            try {
                @Suppress("SpreadOperator")
                String.format(stringValueResource.string.toString(), *fixedParams)
            } catch (t: Throwable) {
                StringResourcesLogger.logT { t }
                stringValueResource.string
            }
        }
    }

    private fun getStringIdResourceString(stringIdResource: StringIdResource, context: Context?): String? {
        return context?.let {
            val fixedParams = stringIdResource.params
                .map {
                    if (it is StringResource) it.getString(context).orEmpty() else it
                }.toTypedArray()

            @Suppress("SpreadOperator")
            getStringSafely(context, stringIdResource.stringId, *fixedParams)
        }
    }

    private fun getStringSafely(context: Context, @StringRes resId: Int, vararg formatArgs: Any): String? {
        return if (formatArgs.isEmpty()) {
            try {
                context.getString(resId)
            } catch (t: Throwable) {
                StringResourcesLogger.logT { t }
                null
            }
        } else {
            try {
                context.getString(resId, formatArgs)
            } catch (t: Throwable) {
                StringResourcesLogger.logT { t }
                getStringSafely(context, resId)
            }
        }
    }
}

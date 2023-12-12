package sk.alival.crutch.stringResources

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString

fun StringResource?.orEmpty(): StringResource = this ?: StringResource.EmptyStringResource()

fun String?.toStringResource(vararg params: Any) = StringResource.StringValueResource(this, *params)

fun Int.toStringResource(vararg params: Any) = StringResource.StringIdResource(this, *params)

fun StringResource?.isEmpty(context: Context?): Boolean = this?.getString(context).isNullOrBlank()

@Composable
fun StringResource?.isEmpty(): Boolean = this?.getString(LocalContext.current).isNullOrBlank()

@Composable
fun StringResource?.asString(): String? = this?.getString(LocalContext.current)

@Composable
fun StringResource?.asAnnotatedString(): AnnotatedString? = this?.getAnnotatedString(LocalContext.current)

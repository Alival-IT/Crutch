package sk.alival.crutch.logging

import sk.alival.crutch.kover.KoverIgnore
import kotlin.reflect.KClass

/**
 * Get [Class] name for [Logs]
 *
 * @return name for logs
 */
@KoverIgnore
fun Class<*>.getNameForLogs(): String = this.canonicalName ?: this.name ?: this.simpleName

/**
 * Get [KClass] name for [Logs]
 *
 * @return name for logs
 */
@KoverIgnore
fun KClass<*>.getNameForLogs(): String = this.qualifiedName ?: this.simpleName ?: this.java.getNameForLogs()

package sk.alival.crutch.logging

import kotlin.reflect.KClass

/**
 * Get [Class] name for [Logs]
 *
 * @return name for logs
 */
fun Class<*>.getNameForLogs(): String = this.canonicalName ?: this.name ?: this.simpleName

/**
 * Get [KClass] name for [Logs]
 *
 * @return name for logs
 */
fun KClass<*>.getNameForLogs(): String = this.qualifiedName ?: this.simpleName ?: this.java.getNameForLogs()

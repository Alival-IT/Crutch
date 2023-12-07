package sk.alival.crutch.states.coroutineHelpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Switching context to Main Immediate
 */
suspend fun <T> withMainImmediate(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    val newContext = context + Dispatchers.Main.immediate
    return withContext(newContext, block)
}

/**
 * Switching context to IO
 */
suspend fun <T> withIO(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    val newContext = context + Dispatchers.IO
    return withContext(newContext, block)
}

/**
 * Switching context to Default
 */
suspend fun <T> withDefault(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    val newContext = context + Dispatchers.Default
    return withContext(newContext, block)
}

/**
 * Switching context to Main
 */
suspend fun <T> withMain(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    val newContext = context + Dispatchers.Main
    return withContext(newContext, block)
}

/**
 * Switching context to Unconfined
 */
suspend fun <T> withUnconfined(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    val newContext = context + Dispatchers.Unconfined
    return withContext(newContext, block)
}

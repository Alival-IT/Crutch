package sk.alival.crutch.states.coroutineHelpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launching new coroutine on IO
 */
fun CoroutineScope.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context + Dispatchers.IO, start, block)

/**
 * Launching new coroutine on Default
 */
fun CoroutineScope.launchDefault(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context + Dispatchers.Default, start, block)

/**
 * Launching new coroutine on Main
 */
fun CoroutineScope.launchMain(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context + Dispatchers.Main, start, block)

/**
 * Launching new coroutine on Main Immediate
 */
fun CoroutineScope.launchMainImmediate(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context + Dispatchers.Main.immediate, start, block)

/**
 * Launching new coroutine on Unconfined
 */
fun CoroutineScope.launchUnconfined(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context + Dispatchers.Unconfined, start, block)

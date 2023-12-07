package sk.alival.crutch.states.coroutineHelpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import sk.alival.crutch.states.States
import sk.alival.crutch.states.tests.StatesTestManager.wrapForTest

/**
 * Launch on io
 *
 * The IO dispatcher is optimized for IO work like reading from the network or disk.
 *
 * [Kotlin official doc](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-i-o.html)
 */
fun States<*>.launchOnIo(job: suspend (CoroutineScope) -> Unit): Job {
    return wrapForTest(
        job,
        statesStreamsContainer.scope.launchIO {
            job(this)
        }
    )
}

/**
 * Launch on default
 *
 * The Default dispatcher is optimized for CPU intensive tasks.
 *
 * [Kotlin official doc](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-default.html)
 */
fun States<*>.launchOnDefault(job: suspend (CoroutineScope) -> Unit): Job {
    return wrapForTest(
        job,
        statesStreamsContainer.scope.launchDefault {
            job(this)
        }
    )
}

/**
 * Launch on unconfined
 *
 * A coroutine dispatcher that is not confined to any specific thread.
 * It executes the initial continuation of a coroutine in the current call-frame and lets the coroutine resume in whatever
 * thread that is used by the corresponding suspending function, without mandating any specific threading policy.
 * Nested coroutines launched in this dispatcher form an event-loop to avoid stack overflows.
 *
 * [Kotlin official doc](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-unconfined.html)
 */
fun States<*>.launchOnUnconfined(job: suspend (CoroutineScope) -> Unit): Job {
    return wrapForTest(
        job,
        statesStreamsContainer.scope.launchUnconfined {
            job(this)
        }
    )
}

/**
 * Launch on main
 *
 * Main is used to perform UI kind operations like Main-thread in Android.
 *
 * [Kotlin official doc](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-main.html)
 */
fun States<*>.launchOnMain(job: suspend (CoroutineScope) -> Unit): Job {
    return wrapForTest(
        job,
        statesStreamsContainer.scope.launchMain {
            job(this)
        }
    )
}

/**
 * Launch on main immediate
 *
 * Returns dispatcher that executes coroutines immediately when it is already in the right context without an additional re-dispatch.
 *
 * [Kotlin official doc](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-main-coroutine-dispatcher/immediate.html)
 */
fun States<*>.launchOnMainImmediate(job: suspend (CoroutineScope) -> Unit): Job {
    return wrapForTest(
        job,
        statesStreamsContainer.scope.launchMainImmediate {
            job(this)
        }
    )
}

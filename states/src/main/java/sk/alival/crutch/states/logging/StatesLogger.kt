package sk.alival.crutch.states.logging

import java.util.concurrent.atomic.AtomicBoolean
import sk.alival.crutch.logging.Logs
import sk.alival.crutch.logging.dm
import sk.alival.crutch.logging.dt

/**
 * States logger to ease up debugging
 *
 */
object StatesLogger {
    /**
     * Is states debug mode enabled. You will see everything that happens in the library in the logcat.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var isStatesDebugModeEnabled: AtomicBoolean = AtomicBoolean(false)

    /**
     * Log method used by [sk.alival.crutch.states.States]
     *
     * Uses [Logs] if enabled, otherwise [println]
     *
     * @param log to log
     */
    inline fun logM(crossinline log: () -> String) {
        if (isStatesDebugModeEnabled.get()) {
            if (Logs.isCustomLogsEnabled() || Logs.isDefaultLogsEnabled()) {
                Logs.dm("StatesLogger", log)
            } else {
                println(log())
            }
        }
    }

    /**
     * Log method used by [sk.alival.crutch.states.States]
     *
     * Uses [Logs] if enabled, otherwise [println]
     *
     * @param log to log
     */
    inline fun logT(crossinline log: () -> Throwable) {
        if (isStatesDebugModeEnabled.get()) {
            if (Logs.isCustomLogsEnabled() || Logs.isDefaultLogsEnabled()) {
                Logs.dt("StatesLogger", log)
            } else {
                log().printStackTrace()
            }
        }
    }
}

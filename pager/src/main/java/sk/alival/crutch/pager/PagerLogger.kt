package sk.alival.crutch.pager

import java.util.concurrent.atomic.AtomicBoolean
import sk.alival.crutch.logging.Logs
import sk.alival.crutch.logging.dm

/**
 * Pager logger to ease up debugging
 *
 */
object PagerLogger {
    /**
     * Is pager debug mode enabled. You will see everything that happens in the library in the logcat.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var isPagerDebugModeEnabled: AtomicBoolean = AtomicBoolean(false)

    /**
     * Log method used by [Pager]
     *
     * Uses [Logs] if enabled, otherwise [println]
     *
     * @param log to log
     */
    inline fun log(crossinline log: () -> String) {
        if (isPagerDebugModeEnabled.get()) {
            if (Logs.isCustomLogsEnabled() || Logs.isDefaultLogsEnabled()) {
                Logs.dm("PagerLogger", log)
            } else {
                println(log())
            }
        }
    }
}

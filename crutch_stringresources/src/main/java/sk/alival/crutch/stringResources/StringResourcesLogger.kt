package sk.alival.crutch.stringResources

import java.util.concurrent.atomic.AtomicBoolean
import sk.alival.crutch.logging.Logs
import sk.alival.crutch.logging.dm
import sk.alival.crutch.logging.dt

/**
 * StringResourcesLogger to ease up debugging
 *
 */
object StringResourcesLogger {
    /**
     * Is stringResources debug mode enabled. You will see everything that happens in the library in the logcat.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var isStringResourcesDebugModeEnabled: AtomicBoolean = AtomicBoolean(false)

    /**
     * Log method used by [StringResource]
     *
     * Uses [Logs] if enabled, otherwise [println]
     *
     * @param log to log
     */
    inline fun logM(crossinline log: () -> String) {
        if (isStringResourcesDebugModeEnabled.get()) {
            if (Logs.isCustomLogsEnabled() || Logs.isDefaultLogsEnabled()) {
                Logs.dm("StringResourcesLogger", log)
            } else {
                println(log())
            }
        }
    }

    /**
     * Log method used by [StringResource]
     *
     * Uses [Logs] if enabled, otherwise [println]
     *
     * @param log to log
     */
    inline fun logT(crossinline log: () -> Throwable) {
        if (isStringResourcesDebugModeEnabled.get()) {
            if (Logs.isCustomLogsEnabled() || Logs.isDefaultLogsEnabled()) {
                Logs.dt("StringResourcesLogger", log)
            } else {
                log().printStackTrace()
            }
        }
    }
}

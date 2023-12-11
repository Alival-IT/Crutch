package sk.alival.crutch.cacheable

import java.util.concurrent.atomic.AtomicBoolean
import sk.alival.crutch.logging.Logs
import sk.alival.crutch.logging.dm

/**
 * Cacheable data logger to ease up debugging
 *
 */
object CacheableDataLogger {
    /**
     * Is Cacheable data debug mode enabled. You will see everything that happens in the library in the logcat.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var isCacheableDataDebugModeEnabled: AtomicBoolean = AtomicBoolean(false)

    /**
     * Log method used by [CacheableData]
     *
     * Uses [Logs] if enabled, otherwise [println]
     *
     * @param log to log
     */
    inline fun log(crossinline log: () -> String) {
        if (isCacheableDataDebugModeEnabled.get()) {
            if (Logs.isCustomLogsEnabled() || Logs.isDefaultLogsEnabled()) {
                Logs.dm("CacheableDataLogger", log)
            } else {
                println(log())
            }
        }
    }
}

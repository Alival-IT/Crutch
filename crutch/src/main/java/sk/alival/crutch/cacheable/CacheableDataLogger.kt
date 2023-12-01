package sk.alival.crutch.cacheable

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Cacheable data logger
 *
 */
object CacheableDataLogger {
    private var isLoggingEnabled: AtomicBoolean = AtomicBoolean(false)

    /**
     * Set cacheable data logger enabled
     *
     * @param isEnabled
     */
    fun setCacheableDataLoggerEnabled(isEnabled: Boolean) {
        this.isLoggingEnabled = AtomicBoolean(isEnabled)
    }

    /**
     * Is cacheable data logger enabled
     *
     */
    fun isCacheableDataLoggerEnabled() = isLoggingEnabled.get()

    /**
     * Helper internal method
     */
    inline fun CacheableDataLogger.log(crossinline log: () -> Unit) {
        if (isCacheableDataLoggerEnabled()) {
            log()
        }
    }
}

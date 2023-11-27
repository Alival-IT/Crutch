package sk.alival.crutch.logging

import timber.log.Timber

/**
 * Logs class to unify logging without depending on a specific logs.
 */
object Logs {

    /**
     * Custom logs
     */
    @Volatile
    var customLogs: CustomLogs? = null

    /**
     * Init Logs, should be called at app startup
     *
     * @param enabledDefaultLogs - whether the default logging system should be enabled
     * @param customLogs - option to add a custom logs [CustomLogs]
     */
    fun init(enabledDefaultLogs: Boolean, customLogs: CustomLogs? = null) {
        Logs.customLogs = customLogs
        if (enabledDefaultLogs) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, tag, message, t)
                    customLogs?.log(priority, tag, message, t)
                }

                override fun createStackElementTag(element: StackTraceElement): String {
                    with(element) {
                        return "($fileName:$lineNumber)"
                    }
                }
            })
        }
    }

    /**
     * Is default logs enabled
     */
    fun isDefaultLogsEnabled() = Timber.treeCount > 0

    /**
     * Disable custom logs
     */
    fun disableDefaultLogs() {
        Timber.uprootAll()
    }

    /**
     * Is custom logs enabled
     */
    fun isCustomLogsEnabled() = customLogs != null
}

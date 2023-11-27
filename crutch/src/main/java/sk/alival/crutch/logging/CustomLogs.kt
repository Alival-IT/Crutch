package sk.alival.crutch.logging

/**
 * CustomLogs can be used to add a custom logs,
 * or for example send every [et] to crash logging tool
 */
interface CustomLogs {
    /**
     * Log message as error message
     *
     * @param message producing string that should be logged
     * @param tag for printing the log with custom tag
     * @receiver lambda that produces the message
     */
    fun em(tag: String? = null, message: () -> String) {}

    /**
     * Log throwable as error message
     *
     * @param throwable producing throwable that should be logged
     * @param tag for printing the log with custom tag
     * @receiver lambda that produces the throwable
     */
    fun et(tag: String? = null, throwable: () -> Throwable) {}

    /**
     * Log message as debug message
     *
     * @param function producing string that should be logged
     * @param tag for printing the log with custom tag
     * @receiver lambda that produces the message
     */
    fun dm(tag: String? = null, function: () -> String) {}

    /**
     * Log throwable as debug message
     *
     * @param function producing throwable that should be logged
     * @param tag for printing the log with custom tag
     * @receiver lambda that produces the throwable
     */
    fun dt(tag: String? = null, function: () -> Throwable) {}

    /**
     * Log message as warning message
     *
     * @param function producing string that should be logged
     * @param tag for printing the log with custom tag
     * @receiver lambda that produces the message
     */
    fun wm(tag: String? = null, function: () -> String) {}

    /**
     * Log throwable as warning message
     *
     * @param function producing throwable that should be logged
     * @param tag for printing the log with custom tag
     * @receiver lambda that produces the throwable
     */
    fun wt(tag: String? = null, function: () -> Throwable) {}

    /**
     * Log every message
     *
     * @param priority - see [android.util.Log]
     * @param tag - custom tag for the message
     * @param message - message to log
     * @param t - throwable to log
     */
    fun log(priority: Int, tag: String?, message: String, t: Throwable?) {}
}

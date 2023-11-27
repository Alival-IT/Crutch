package sk.alival.crutch.logging

import timber.log.Timber

/**
 * @see [CustomLogs.em]
 */
inline fun Logs.em(tag: String? = null, crossinline message: () -> String): Logs {
    if (isDefaultLogsEnabled()) {
        Timber.also {
            if (tag != null) {
                it.tag(tag)
            }
        }.e(message())
    }
    getCustomLogs()?.em { message() }
    return this
}

/**
 * @see [CustomLogs.et]
 */
inline fun Logs.et(tag: String? = null, crossinline t: () -> Throwable): Logs {
    if (isDefaultLogsEnabled()) {
        Timber.also {
            if (tag != null) {
                it.tag(tag)
            }
        }.e(t())
    }
    getCustomLogs()?.et { t() }
    return this
}

/**
 * @see [CustomLogs.dm]
 */
inline fun Logs.dm(tag: String? = null, crossinline message: () -> String): Logs {
    if (isDefaultLogsEnabled()) {
        Timber.also {
            if (tag != null) {
                it.tag(tag)
            }
        }.d(message())
    }
    getCustomLogs()?.dm { message() }
    return this
}

/**
 * @see [CustomLogs.dt]
 */
inline fun Logs.dt(tag: String? = null, crossinline t: () -> Throwable): Logs {
    if (isDefaultLogsEnabled()) {
        Timber.also {
            if (tag != null) {
                it.tag(tag)
            }
        }.d(t())
    }
    getCustomLogs()?.dt { t() }
    return this
}

/**
 * @see [CustomLogs.wm]
 */
inline fun Logs.wm(tag: String? = null, crossinline message: () -> String): Logs {
    if (isDefaultLogsEnabled()) {
        Timber.also {
            if (tag != null) {
                it.tag(tag)
            }
        }.w(message())
    }
    getCustomLogs()?.wm { message() }
    return this
}

/**
 * @see [CustomLogs.wt]
 */
inline fun Logs.wt(tag: String? = null, crossinline t: () -> Throwable): Logs {
    if (isDefaultLogsEnabled()) {
        Timber.also {
            if (tag != null) {
                it.tag(tag)
            }
        }.w(t())
    }
    getCustomLogs()?.wt { t() }
    return this
}

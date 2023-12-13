package sk.alival.crutch.states.savedStateHandleManager

import androidx.lifecycle.SavedStateHandle
import sk.alival.crutch.states.logging.StatesLogger
import kotlin.reflect.KClass

interface SavedStateHandleManager {
    companion object {
        fun createKey(clazz: KClass<*>, customKey: String?): String {
            return customKey ?: clazz.java.canonicalName ?: clazz.java.name ?: clazz.java.simpleName
        }
    }

    fun <T> getValue(key: String): T?
    fun <T> setValue(key: String, value: T?)
    fun <T> removeValue(key: String)
}

class SavedStateHandleManagerImpl(
    private val savedStateHandle: SavedStateHandle
) : SavedStateHandleManager {

    override fun <T> setValue(key: String, value: T?) {
        try {
            StatesLogger.logM { "Preparing to store $value with key $key into SavedStateHandle." }
            savedStateHandle[key] = value
            StatesLogger.logM { "Successfully stored $value with key $key into SavedStateHandle." }
        } catch (t: Throwable) {
            StatesLogger.logM { "Failed storing $value with key $key into SavedStateHandle." }
            StatesLogger.logT { t }
        }
    }

    override fun <T> getValue(key: String): T? {
        return try {
            StatesLogger.logM { "Preparing to get with key $key from SavedStateHandle." }
            savedStateHandle.get<T>(key)?.also {
                StatesLogger.logM { "Successfully retrieved value $it with key $key from SavedStateHandle." }
            }
        } catch (t: Throwable) {
            StatesLogger.logM { "Failed to get with key $key from SavedStateHandle." }
            StatesLogger.logT { t }
            null
        }
    }

    override fun <T> removeValue(key: String) {
        try {
            StatesLogger.logM { "Preparing to remove data with key $key from SavedStateHandle." }
            savedStateHandle.remove<T>(key)?.also {
                StatesLogger.logM { "Successfully removed value $it with key $key from SavedStateHandle." }
            }
        } catch (t: Throwable) {
            StatesLogger.logM { "Failed to remove value with key $key from SavedStateHandle." }
            StatesLogger.logT { t }
        }
    }
}

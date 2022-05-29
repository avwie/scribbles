package common.persistence

import kotlinx.coroutines.flow.SharedFlow

interface KeyValueStore<T> {
    data class Update<T>(val key: String, val oldValue: T?, val newValue: T)

    val updates: SharedFlow<Update<T>>

    suspend fun store(key: String, item: T)
    fun retrieve(key: String): T?
}
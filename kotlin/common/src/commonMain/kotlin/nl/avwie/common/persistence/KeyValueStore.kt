package nl.avwie.common.persistence

interface KeyValueStore<T> {
    suspend fun contains(key: String): Boolean
    suspend fun set(key: String, item: T)
    suspend fun get(key: String): T?
    suspend fun remove(key: String)
}
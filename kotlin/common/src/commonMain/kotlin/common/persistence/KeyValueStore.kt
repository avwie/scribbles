package common.persistence

interface KeyValueStore<T> {
    fun store(key: String, item: T)
    fun retrieve(key: String): T?
}
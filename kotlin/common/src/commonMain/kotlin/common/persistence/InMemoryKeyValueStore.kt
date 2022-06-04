package common.persistence

class InMemoryKeyValueStore<T> : KeyValueStore<T> {

    private val items = mutableMapOf<String, T>()

    override suspend fun contains(key: String): Boolean {
        return items.contains(key)
    }

    override suspend fun set(key: String, item: T) {
        items[key] = item
    }

    override suspend fun get(key: String): T? = items[key]

    override suspend fun remove(key: String) {
        items.remove(key)
    }
}
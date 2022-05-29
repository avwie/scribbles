package common.persistence

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InMemoryKeyValueStore<T> : KeyValueStore<T> {

    private val items = mutableMapOf<String, T>()

    private val _updates = MutableSharedFlow<KeyValueStore.Update<T>>()
    override val updates: SharedFlow<KeyValueStore.Update<T>> = _updates.asSharedFlow()

    override suspend fun store(key: String, item: T) {
        val oldItem = retrieve(key)
        items[key] = item
        _updates.emit(KeyValueStore.Update(key, oldItem, item))
    }

    override fun retrieve(key: String): T? = items[key]
}
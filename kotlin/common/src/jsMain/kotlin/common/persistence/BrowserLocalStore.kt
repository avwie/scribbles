package common.persistence

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BrowserLocalStore(
    val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
) : KeyValueStore<String> {
    private val storage = window.localStorage

    private val _updates = MutableSharedFlow<KeyValueStore.Update<String>>()
    override val updates: SharedFlow<KeyValueStore.Update<String>> = _updates.asSharedFlow()

    init {
        window.onstorage = { event ->
            scope.launch {
                _updates.emit(KeyValueStore.Update(event.key!!, event.oldValue, event.newValue!!))
            }
        }
    }

    override suspend fun store(key: String, item: String) {
        val oldItem = retrieve(key)
        storage.setItem(key, item)
        _updates.emit(KeyValueStore.Update(key, oldItem, item))
    }

    override fun retrieve(key: String): String? {
        return storage.getItem(key)
    }
}
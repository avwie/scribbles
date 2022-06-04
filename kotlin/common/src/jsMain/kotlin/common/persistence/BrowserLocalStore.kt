package common.persistence

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BrowserLocalStore : KeyValueStore<String> {
    private val storage = window.localStorage

    override suspend fun contains(key: String): Boolean {
        return storage.getItem(key) != null
    }

    override suspend fun set(key: String, item: String) {
        storage.setItem(key, item)
    }

    override suspend fun get(key: String): String? {
        return storage.getItem(key)
    }

    override suspend fun remove(key: String) {
        if (!contains(key)) return
        storage.removeItem(key)
    }
}
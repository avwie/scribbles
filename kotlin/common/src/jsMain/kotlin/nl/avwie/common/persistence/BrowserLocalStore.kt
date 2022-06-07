package nl.avwie.common.persistence

import kotlinx.browser.window
import org.w3c.dom.Storage

class BrowserLocalStore(private val storage: Storage) : KeyValueStore<String> {

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

fun browserLocalStorage() = BrowserLocalStore(window.localStorage)
fun browserSessionStorage() = BrowserLocalStore(window.sessionStorage)
package nl.avwie.common.persistence

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryKeyValueStoreTests {

    @Test
    fun updates() = runTest {
        /*val store = InMemoryKeyValueStore<String>()
        val updates = mutableListOf<KeyValueStore.Update<String>>()

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            store.updates.collect {
                updates.add(it)
            }
        }
        store.set("Foo", "Bar")
        store.set("Foo", "Bat")
        job.cancel()

        assertEquals(2, updates.size)
        assertEquals(KeyValueStore.Update("Foo", null, "Bar"), updates[0])
        assertEquals(KeyValueStore.Update("Foo", "Bar", "Bat"), updates[1])*/
    }
}
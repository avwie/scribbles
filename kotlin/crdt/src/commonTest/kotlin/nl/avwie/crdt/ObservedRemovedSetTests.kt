package nl.avwie.crdt

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObservedRemovedSetTests {

    @Test
    fun mergeAndSerialize() {
        val a = observedRemovedSetOf(1, 2, 3)
        val b = observedRemovedSetOf(2, 3, 4)

        a.remove(2)
        val c = merge(a, b)

        assertTrue { c.contains(1) }
        assertFalse { c.contains(2) }

        val serialized = Json.encodeToString(c)
        val deserialized = Json.decodeFromString<ObservedRemovedSet<Int, Int>>(serialized)

        assertTrue { deserialized.contains(1) }
        assertFalse { deserialized.contains(2) }
        assertTrue { deserialized.contains(3) }
        assertTrue { deserialized.contains(4) }
    }
}
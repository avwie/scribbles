package nl.avwie.crdt.convergent

import nl.avwie.common.sleep
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.*

class MergeableSetTests {

    @Test
    fun add() {
        val a = mergeableSetOf(1, 2, 3).add(4)
        assertTrue { listOf(1, 2, 3, 4).all { a.contains(it) } }
    }

    @Test
    fun addWithTombstone() {
        val a = mergeableSetOf(1, 3).remove(2).add(2)
        assertTrue { a.contains(2) }

        val b = mergeableSetOf(1, 2, 3).remove(2).add(2)
        assertFalse { b.contains(2) }
    }

    @Test
    fun addAll() {
        val a = mergeableSetOf(1, 2, 3).addAll(4, 5, 6)
        assertTrue { listOf(1, 2, 3, 4, 5, 6).all { a.contains(it) } }
    }

    @Test
    fun remove() {
        val a = mergeableSetOf(1, 2, 3).remove(2)
        assertTrue { listOf(2).none { a.contains(it) } }
    }

    @Test
    fun removeAll() {
        val a = mergeableSetOf(1, 2, 3).removeAll(1, 2)
        assertTrue { listOf(1, 2).none { a.contains(it) } }
    }

    @Test
    fun merge() {
        val a = mergeableSetOf(1, 2, 3)
        val b = mergeableSetOf(2, 3, 4)
        val c = merge(a, b)

        assertTrue { listOf(1, 2, 3, 4).all { c.contains(it) } }
    }

    @Test
    fun mergeWithTombstones() {
        val a = mergeableSetOf(1, 2, 3).remove(2)
        val b = mergeableSetOf(2, 3, 4)
        val c = merge(a, b)

        assertTrue { listOf(1, 3, 4).all { c.contains(it) } }
        assertFalse { c.contains(2) }
    }

    @Test
    fun serialize() {
        val a = mergeableSetOf(1, 2, 3).remove(2)
        val b = mergeableSetOf(2, 3, 4)
        val c = merge(a, b)

        val serialized = Json.encodeToString(c)
        val deserialized = Json.decodeFromString<MergeableSet<Int, Int>>(serialized)
        assertEquals(c, deserialized)
    }

    @Test
    fun mergeWithMergeables() {
        val keyResolver = object : KeyResolver<MergeableValue<String>, String> {
            override fun key(item: MergeableValue<String>): String = item.value.lowercase()
        }

        val a = mergeableSetOf(mergeableValueOf("Foo"), mergeableValueOf("Bar"), keyResolver = keyResolver)
        sleep(1)
        val b = mergeableSetOf(mergeableValueOf("BAR"), mergeableValueOf("Baz"), keyResolver = keyResolver)
        val c = merge(a, b)

        assertTrue { c.contains(mergeableValueOf("BAR")) }
        assertFalse { c.contains(mergeableValueOf("Bar")) }
    }
}
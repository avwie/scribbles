package nl.avwie.crdt.convergent

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.common.sleep
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MergeableMapTests {

    @Test
    fun equality() {
        val mapA = mergeableMapOf("a" to 1, "b" to 2).put("c", 3)
        val mapB = mergeableMapOf("a" to 1, "b" to 2).put("c", 3)
        assertEquals(mapA, mapB)
    }

    @Test
    fun add() {
        val map = mergeableMapOf("a" to 1, "b" to 2).put("c", 3)
        assertTrue { map.contains("c") }
        assertEquals(3, map["c"])
    }

    @Test
    fun remove() {
        val map = mergeableMapOf("a" to 1, "b" to 2).remove("a")
        assertEquals(null, map["a"])
    }

    @Test
    fun addWithTombstone() {
        val map = mergeableMapOf("a" to 1, "b" to 2).remove("c").put("c", 3)
        assertEquals(3, map["c"])

        val map2 = map.remove("c").put("c", 4)
        assertEquals(4, map2["c"])
    }

    @Test
    fun mergeWithTombstone() {
        val map = mergeableMapOf("a" to 1, "b" to 2)
        val map2 = mergeableMapOf("a" to 1, "b" to 2).remove("b")
        val merged = map.merge(map2)
        assertFalse { merged.contains("b") }
    }

    @Test
    fun mergeWithTombstoneReversed() {
        val map2 = mergeableMapOf("a" to 1, "b" to 2).remove("b")
        sleep(1)
        val map = mergeableMapOf("a" to 1, "b" to 2)
        val merged = map.merge(map2)
        assertTrue { merged.contains("b") }
    }

    @Test
    fun fuzzing() {
        val keysAndValues = (0 until 1000).toList()

        val added = mutableSetOf<Int>()
        val removed = mutableSetOf<Int>()

        fun generate() = keysAndValues.fold(mergeableMapOf<Int, String>()) { acc, i ->
            when  {
                Random.nextFloat() < .7 -> {
                    added.add(i)
                    acc.put(i, i.toString())
                }
                else -> acc
            }
        }

        fun remove(map: MergeableMap<Int, String>) = keysAndValues.fold(map) { acc, i ->
            when {
                Random.nextFloat() < .1 -> {
                    removed.add(i)
                    acc.remove(i)
                }
                else -> acc
            }
        }

        val merged = merge(listOf(generate(), generate(), generate()).map { remove(it) })
        val totals = added - removed
        assertEquals(totals.size, merged.keys.size)
    }

    @Test
    fun serialization() {
        val map = mergeableMapOf("a" to 1, "b" to 2, "c" to 3).remove("c")
        val serialized = Json.encodeToString(map)
        val deserialized = Json.decodeFromString<MergeableMap<String, Int>>(serialized)
        assertEquals(map, deserialized)
    }
}
package nl.avwie.crdt.convergent

import kotlinx.datetime.Clock
import nl.avwie.common.sleep
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MergeableValueTests {

    @Test
    fun equalTimestamps() {
        val timestamp = Clock.System.now()
        val a = MergeableValue(1, timestamp)
        val b = MergeableValue(2, timestamp)

        val c1 = a.merge(b)
        val c2 = b.merge(a)
        assertEquals(c1, c2)
    }

    @Test
    @JsName("equalsTest")
    fun equals() {
        val a = mergeableValueOf(1)
        sleep(1)
        val b = mergeableValueOf(1)
        assertEquals(a, b)

        val c = mergeableValueOf(2)
        assertNotEquals(a, c)
    }

    @Test
    @JsName("hashcodeTest")
    fun hashcode() {
        val a = mergeableValueOf(1)
        sleep(1)
        val b = mergeableValueOf(1)

        val set = mutableSetOf(a)
        assertTrue { set.contains(b) }
    }

    @Test
    fun merge() {
        val a = mergeableValueOf(1)
        sleep(1)
        val b = mergeableValueOf(2)
        sleep(1)
        val c = mergeableValueOf(3)

        assertEquals(3, merge(a, b, c).value)
        assertEquals(3, merge(a, c, b).value)
        assertEquals(3, merge(b, c, a).value)
        assertEquals(3, merge(b, a, c).value)
        assertEquals(3, merge(c, a, b).value)
        assertEquals(3, merge(c, b, a).value)
    }

    @Test
    fun serialize() {
        val x = mergeableValueOf("Foo")
        val serialized = Json.encodeToString(x)
        val deserialized = Json.decodeFromString<MergeableValue<String>>(serialized)
        assertEquals(x, deserialized)
    }

    @Test
    fun fuzz() {
        val values = (0 until 1000).map { mergeableValueOf(it) }
        val mergedA = values.shuffled().reduce { a, b -> a.merge(b) }
        val mergedB = values.shuffled().reduce { a, b -> a.merge(b) }
        assertEquals(mergedA, mergedB)
    }
}
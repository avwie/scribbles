package nl.avwie.crdt.convergent

import common.crdt.convergent.MergeableValue
import common.crdt.convergent.mergeableValueOf
import common.sleep
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

        assertEquals(3, common.crdt.convergent.merge(a, b, c).value)
        assertEquals(3, common.crdt.convergent.merge(a, c, b).value)
        assertEquals(3, common.crdt.convergent.merge(b, c, a).value)
        assertEquals(3, common.crdt.convergent.merge(b, a, c).value)
        assertEquals(3, common.crdt.convergent.merge(c, a, b).value)
        assertEquals(3, common.crdt.convergent.merge(c, b, a).value)
    }

    @Test
    fun serialize() {
        val x = mergeableValueOf("Foo")
        val serialized = Json.encodeToString(x)
        val deserialized = Json.decodeFromString<MergeableValue<String>>(serialized)
        assertEquals(x, deserialized)
    }
}
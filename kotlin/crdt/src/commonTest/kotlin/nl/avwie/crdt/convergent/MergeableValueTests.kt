package nl.avwie.crdt.convergent

import common.sleep
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

        assertEquals(3, merge(a, b, c).value)
        assertEquals(3, merge(a, c, b).value)
        assertEquals(3, merge(b, c, a).value)
        assertEquals(3, merge(b, a, c).value)
        assertEquals(3, merge(c, a, b).value)
        assertEquals(3, merge(c, b, a).value)
    }
}
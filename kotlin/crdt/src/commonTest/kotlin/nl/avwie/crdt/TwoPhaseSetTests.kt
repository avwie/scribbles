package nl.avwie.crdt

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TwoPhaseSetTests {

    @Test
    fun merge() {
        val a = twoPhaseSetOf(1, 2, 3)
        val b = twoPhaseSetOf(2, 3, 4)

        a.remove(2)
        val c = merge(a, b)

        assertTrue { c.contains(1) }
        assertFalse { c.contains(2) }
    }
}
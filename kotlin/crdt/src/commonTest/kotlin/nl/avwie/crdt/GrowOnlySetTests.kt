package nl.avwie.crdt

import kotlin.test.Test
import kotlin.test.assertEquals

class GrowOnlySetTests {

    @Test
    fun merge() {
        val a = growOnlySetOf(1, 2, 3)
        val b = growOnlySetOf(2, 3, 4)

        a.add(5)
        val c = merge(a, b)

        assertEquals(4, a.size)
        assertEquals(3, b.size)
        assertEquals(5, c.size)
    }
}
package nl.avwie.crdt.convergent

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MergeableMapTests {

    @Test
    fun add() {
        val map = mergeableMapOf("a" to 1, "b" to 2).put("c", 3)
        assertTrue { map.contains("c") }
        assertEquals(3, map["c"])
    }

    @Test
    fun addWithTombstone() {
        val map = mergeableMapOf("a" to 1, "b" to 2).remove("c").put("c", 3)
        assertEquals(3, map["c"])

        val map2 = map.remove("c").put("c", 4)
        assertEquals(null, map2["c"])
    }
}
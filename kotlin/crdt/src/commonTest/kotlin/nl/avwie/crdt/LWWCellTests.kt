package nl.avwie.crdt

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class LWWCellTests {

    @Test
    fun serialize() {
        val test = "Foobar".toLWWCell()
        val serialized = Json.encodeToString(test)
        val deserialized = Json.decodeFromString<LWWCell<String>>(serialized)
        assertEquals("Foobar", deserialized.value)
    }
}
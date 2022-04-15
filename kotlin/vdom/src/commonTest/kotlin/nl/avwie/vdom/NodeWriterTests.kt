package nl.avwie.vdom

import nl.avwie.dom.xml
import kotlin.test.Test

class NodeWriterTests {

    @Test
    fun simple() {
        val dom = xml("foo", "bar" to "baz", "quux" to "quix") {
            "childA"()
            "childB" ("some" to "data") {
                "childC"()
            }
        }

        val result = dom.toNode()
    }
}
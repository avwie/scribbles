package nl.avwie.vdom

import nl.avwie.dom.xml
import kotlin.test.Test

class DifferTests {

    @Test
    fun attributes() {
        val left = xml("foo", "bar" to "baz", "quux" to "quix") {
            "childA"()
            "childB" ("some" to "data") {
                "childC"()
            }
        }.toNode()

        val right = xml("foo", "bar" to "bat", "quux" to "quix") {
            "childA"()
            "childB" ("some" to "data") {
                "childC"()
            }
        }.toNode()

        val diff = Differ.diff(left, right)
    }

    @Test
    fun children() {
        val left = xml("foo", "bar" to "bat", "quux" to "quix") {
            "childA"()
            "childB" ("some" to "data") {
                "childC"()
            }
        }.toNode()

        val right = xml("foo", "bar" to "bat", "quux" to "quix") {
            "childA"()
            "childB" ("some" to "data-modified") {
                "childC"("attr" to "added")
            }
        }.toNode()

        val diff = Differ.diff(left, right)
    }

    @Test
    fun childrenAdded() {
        val left = xml("foo", "bar" to "bat", "quux" to "quix") {
            "childA"()
        }.toNode()

        val right = xml("foo", "bar" to "bat", "quux" to "quix") {
            "childB" ("some" to "data") {
                "childC"()
            }
            "childA"()
        }.toNode()

        val diff = Differ.diff(left, right)
    }
}
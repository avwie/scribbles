package nl.avwie.dom

import kotlin.test.Test
import kotlin.test.assertEquals


class DomTests {

    @Test
    fun simpleXmlTest() {
        val foo = xml("foo",
            "arg1" to "bar",
            "arg2" to 42
        ) {
            "inner"(
                "baz" to "bat",
                "quux" to 1231.44
            )
        }

        val result = foo.renderAsString(prettyPrint = true)
        val expected = """
            <foo arg1="bar" arg2="42">
                <inner baz="bat" quux="1231.44" />
            </foo>
        """.trimIndent() + "\n"
        assertEquals(expected, result)
    }
}
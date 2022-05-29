package common.routing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocationTests {

    @Test
    fun parse() {
        assertEquals(Location("/"), Location.parse("/"))
        assertEquals(Location("/foo"), Location.parse("/foo"))
        assertEquals(Location("/foo", "q=1"), Location.parse("/foo?q=1"))
        assertEquals(Location("/foo", null, "baz"), Location.parse("/foo#baz"))

        assertFailsWith<IllegalArgumentException> { Location.parse("/foo?bar?baz") }
        assertFailsWith<IllegalArgumentException> { Location.parse("/foo?#bar#baz") }
        assertFailsWith<IllegalArgumentException> { Location.parse("/foo#bar?q=1") }
    }
}
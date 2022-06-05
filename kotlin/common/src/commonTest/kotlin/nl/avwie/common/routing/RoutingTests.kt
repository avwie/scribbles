package nl.avwie.common.routing

import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTests {

    val routing = createRouting("error") {
        matchPathName("/") { "root" }
        matchPathName("/foo") { "foo" }
        matchPathName("/bar") { "bar" }
    }

    @Test
    fun basic() {
        assertEquals("root", routing.getRoute("/"))
        assertEquals("foo", routing.getRoute("/foo"))
        assertEquals("bar", routing.getRoute("/bar"))
    }

    @Test
    fun error() {
        assertEquals("error", routing.getRoute("/missing"))
    }
}
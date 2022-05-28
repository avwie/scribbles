package common.routing

import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryHistoryTests {

    @Test
    fun initialize() {
        val history = InMemoryHistory(Location("/"))
        assertEquals(Location("/"), history.activeLocation.value)
    }

    @Test
    fun navigate() {
        val history = InMemoryHistory(Location("/"))
        history.push(Location("/foo"))
        assertEquals(Location("/foo"), history.activeLocation.value)

        history.back()
        assertEquals(Location("/"), history.activeLocation.value)

        history.forward()
        assertEquals(Location("/foo"), history.activeLocation.value)
    }

    @Test
    fun navigateAndPush() {
        val history = InMemoryHistory(Location("/"))
        history.push(Location("/foo"))
        history.push(Location("/bar"))
        history.back()
        history.push(Location("/baz"))
        assertEquals(Location("/baz"), history.activeLocation.value)
        history.forward()
        assertEquals(Location("/baz"), history.activeLocation.value)
    }
}
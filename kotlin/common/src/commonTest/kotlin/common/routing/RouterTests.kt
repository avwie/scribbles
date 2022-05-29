package common.routing

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RouterTests {

    val routes = listOf("/" to "root", "/foo" to "foo", "/bar" to "bar")

    val routing = createRouting("error") {
        routes.forEach { (pathName, route) ->
            matchPathName(pathName) { route }
        }
    }

    @Test
    fun basic() {
        val history = InMemoryHistory(initialPathName = "/")
        val router = Router(history, routing)

        routes.forEach { (pathName, expected) ->
            history.push(pathName)
            assertEquals(expected, router.activeRoute.value)
        }

        history.push("non existing")
        assertEquals("error", router.activeRoute.value)
    }
}
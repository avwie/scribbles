package nl.avwie.common.routing

import nl.avwie.common.uuid
import org.w3c.dom.Window

class BrowserHistory(
    private val window: Window,
    private val memory: InMemoryHistory
) : History by memory {

    init {
        updateWindowHistory()
        window.onpopstate = { event ->
            when ((event.state as? String)?.let { uuid(it) }) {
                null -> null
                memory.peekForwardEntry()?.id -> forward()
                memory.peekBackEntry()?.id -> back()
                else -> null
            }
        }
    }

    constructor(): this(
        kotlinx.browser.window,
        InMemoryHistory(Location.parse(kotlinx.browser.window.location.pathname))
    )

    override fun push(location: Location) {
        memory.push(location)
        updateWindowHistory()
    }

    override fun forward() {
        memory.forward()
        updateWindowHistory()
    }

    override fun back() {
        memory.back()
        updateWindowHistory()
    }

    private fun updateWindowHistory() {
        val state = (window.history.state as? String)?.let { uuid(it) }
        if (state != memory.activeEntry.value.id) {
            window.history.pushState(memory.activeEntry.value.id.toString(), "", activeLocation.value.toURL())
        }
    }
}
package common.routing

import kotlinx.browser.window

class BrowserHistory(
    val history: org.w3c.dom.History,
    val memory: InMemoryHistory
) : History by memory {

    constructor(): this(window.history, InMemoryHistory(Location.parse(window.location.pathname)))

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
        window.history.pushState(null, "", activeLocation.value.toURL())
    }
}
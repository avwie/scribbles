package nl.avwie.vdom

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.events.Event

class BrowserDocumentTarget(private val root: Element) : Renderer.Target<Element> {

    private val listeners = mutableMapOf<Pair<String, Element>, (Event) -> Unit>()

    override fun createElement(name: String, namespace: String?): Element {
        return document.createElementNS(namespace, name)
    }

    override fun setAttribute(element: Element, key: String, value: String) {
        element.asDynamic().setAttribute(key, value) // <- this is waaay faster
    }

    override fun setText(element: Element, text: String?) {
        element.textContent = text
    }

    override fun appendChild(container: Element?, child: Element) {
        (container ?: root).appendChild(child)
    }

    override fun remove(element: Element) {
        element.remove()
    }

    override fun removeAttribute(element: Element, key: String) {
        element.removeAttribute(key)
    }

    override fun setEventHandler(element: Element, event: String, callback: () -> Unit) {
        listeners[event to element] = { callback() }
        element.addEventListener(event, { callback() })
    }

    override fun removeEventHandler(element: Element, event: String) {
        element.removeEventListener(event, listeners.remove(event to element))
    }
}
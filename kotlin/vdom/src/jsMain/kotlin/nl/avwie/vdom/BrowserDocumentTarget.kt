package nl.avwie.vdom

import kotlinx.browser.document
import org.w3c.dom.Element

class BrowserDocumentTarget(private val root: Element) : Renderer.Target<Element> {

    override fun createElement(name: String, namespace: String?): Element {
        return document.createElementNS(namespace, name)
    }

    override fun setAttribute(element: Element, key: String, value: String) {
        element.setAttribute(key, value)
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
}
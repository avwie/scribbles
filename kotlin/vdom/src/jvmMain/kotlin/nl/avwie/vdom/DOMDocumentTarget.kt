package nl.avwie.vdom

import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class DOMDocumentTarget(private val document: Document) : Renderer.Target<Element> {

    constructor(): this(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())

    override fun createElement(name: String, namespace: String?): Element {
        return namespace?.let { document.createElementNS(namespace, name) } ?: document.createElement(name)
    }

    override fun setAttribute(element: Element, key: String, value: String) {
        element.setAttribute(key, value)
    }

    override fun setText(element: Element, text: String?) {
        element.textContent = text
    }

    override fun appendChild(container: Element?, child: Element) {
        if (container != null) container.appendChild(child)
        else document.appendChild(child)
    }

    override fun remove(element: Element) {
        element.parentNode.removeChild(element)
    }

    override fun removeAttribute(element: Element, key: String) {
        element.removeAttribute(key)
    }
}
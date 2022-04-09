package nl.avwie.dom

import kotlinx.browser.document
import org.w3c.dom.Element

class BrowserWriter : Writer<Element> {

    private var _result: Element? = null
    private val stack = ArrayDeque<Element>()

    override fun beginElement(name: String, namespace: String?) {
        val element = document.createElementNS(namespace, name)
        stack.firstOrNull()?.appendChild(element)
        stack.addFirst(element)
    }

    override fun endElement() {
        _result = stack.removeFirst()
    }

    override fun writeAttribute(name: String, value: String) {
        stack.first().setAttribute(name, value)
    }

    override fun writeText(text: String) {
        stack.first().textContent = text
    }

    override fun result(): Element = _result!!
}
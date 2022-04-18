package nl.avwie.vdom

import kotlinx.browser.document
import kotlinx.dom.clear
import org.w3c.dom.Element

class BrowserRendererTarget(private val container: Element) : Renderer.Target {

    private val context = ArrayDeque<Element?>()

    override fun down() {
        context.addFirst(context.firstOrNull()?.firstElementChild)
    }

    override fun up() {
        context.removeFirst()
    }

    override fun next() {
        context.addFirst(context.removeFirstOrNull()?.nextElementSibling)
    }

    override fun reset() {
        context.clear()
        context.addFirst(container)
    }

    override fun clear() {
        container.clear()
    }

    override fun createElement(node: Node) {
        val parent = when {
            context.firstOrNull() != null -> context.first()!!.parentElement!!
            else -> context.first { it != null }!!
        }

        val element = innerCreateElement(node)
        parent.insertBefore(element, context.firstOrNull())
    }

    private fun innerCreateElement(node: Node): Element {
        val element = document.createElementNS(node.namespace, node.name)
        node.attributes.forEach { (key, value) -> element.setAttribute(key, value) }
        element.textContent = node.text
        node.childNodes.forEach { child -> element.appendChild(innerCreateElement(child)) }
        return element
    }

    override fun removeElement() {
        context.removeFirstOrNull()?.also { element ->
            element.remove()
            context.addFirst(element.nextElementSibling)
        }
    }

    override fun updateAttribute(name: String, value: String) {
        context.firstOrNull()?.setAttribute(name, value)
    }

    override fun removeAttribute(name: String) {
        context.firstOrNull()?.removeAttribute(name)
    }

    override fun setText(name: String) {
        context.firstOrNull()?.textContent = name
    }
}
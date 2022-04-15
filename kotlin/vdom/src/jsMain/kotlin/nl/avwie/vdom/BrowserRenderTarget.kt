package nl.avwie.vdom

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.get

class BrowserRenderTarget(val root: Element) : RenderTarget<Element> {

    private lateinit var context: Context;

    init {
        reset()
    }

    override fun beginElement(tag: String, namespace: String?) {
        context = context.beginElement(tag, namespace)
    }

    override fun endElement() {
        context = context.endElement()!!
    }

    override fun writeAttribute(name: String, value: String) {
        context = context.setAttribute(name, value)
    }

    override fun writeText(text: String) {
        context = context.writeText(text)
    }

    override fun removeElement() {
        TODO("Not yet implemented")
    }

    override fun next() {
        TODO("Not yet implemented")
    }

    override fun removeAttribute(name: String) {
        TODO("Not yet implemented")
    }

    override fun reset() {
        context = Context(root, null)
    }

    override fun result(): Element {
        return root
    }

    private data class Context(
        val element: Element,
        val parentContext: Context?,
        val cursor: Int = 0
    ) {
        fun beginElement(tag: String, namespace: String?): Context {
            val child = document.createElementNS(namespace, tag)
            val insertBefore = this.element.childNodes[cursor]
            if (insertBefore != null) {
                this.element.insertBefore(insertBefore, child)
            } else {
                this.element.appendChild(child)
            }
            return Context(child, copy(cursor = cursor + 1))
        }

        fun endElement(): Context? {
            return parentContext
        }

        fun setAttribute(name: String, value: String): Context {
            element.setAttribute(name, value)
            return this
        }

        fun writeText(text: String): Context {
            element.textContent = text
            return this
        }

        fun remove(): Context? {
            element.remove()
            return parentContext
        }
    }
}
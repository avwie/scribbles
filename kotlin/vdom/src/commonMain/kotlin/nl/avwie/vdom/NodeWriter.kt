package nl.avwie.vdom

import nl.avwie.dom.Writer
import nl.avwie.vdom.Node.Companion.NAMESPACE_ATTR_NAME
import nl.avwie.vdom.Node.Companion.TEXT_ATTR_NAME

class NodeWriter : Writer<Node> {

    private var result: Node? = null

    private val current = ArrayDeque<Node>()

    override fun beginElement(tag: String, namespace: String?) {
        current.addFirst(Node(tag, mapOf(NAMESPACE_ATTR_NAME to (namespace ?: "")), listOf()))
    }

    override fun endElement() {
        result = current.removeFirst()
        current.removeFirstOrNull()?.let {
            it.copy(children = it.children + result!!)
        }?.also {
            current.addFirst(it)
        }
    }

    override fun writeAttribute(name: String, value: String) {
        current.removeFirst().let {
            it.copy(attributes = it.attributes + (name to value))
        }.also { current.addFirst(it) }
    }

    override fun writeText(text: String) {
        current.removeFirst().let {
            it.copy(attributes = it.attributes + (TEXT_ATTR_NAME to text))
        }.also { current.addFirst(it) }
    }

    override fun result(): Node = result!!
}
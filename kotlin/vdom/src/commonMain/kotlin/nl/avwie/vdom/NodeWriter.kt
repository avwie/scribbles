package nl.avwie.vdom

import nl.avwie.dom.Writer

class NodeWriter : Writer<Node> {

    private var result: Node? = null

    private val current = ArrayDeque<Node>()

    override fun beginElement(tag: String, namespace: String?) {
        current.addFirst(Node(tag, mapOf("namespace" to (namespace ?: "")), listOf()))
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
            it.copy(attributes = it.attributes + ("text" to text))
        }.also { current.addFirst(it) }
    }

    override fun result(): Node = result!!
}
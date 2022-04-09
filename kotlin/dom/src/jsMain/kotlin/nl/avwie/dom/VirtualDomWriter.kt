package nl.avwie.dom

import externals.virtualDom.VNode
import externals.virtualDom.h
import kotlin.js.json

class VirtualDomWriter : Writer<VNode> {

    private var _result: VNode? = null

    private val elements = ArrayDeque<Pair<String, String?>>()
    private val children = ArrayDeque<MutableList<dynamic>>()
    private val attributes = ArrayDeque<MutableList<Pair<String, String>>>()

    override fun beginElement(name: String, namespace: String?) {
        elements.addFirst(name to namespace)
        children.addFirst(mutableListOf())
        attributes.addFirst(mutableListOf())
    }

    override fun endElement() {
        val (element, namespace) = elements.removeFirst()
        val attributes = json(*attributes.removeFirst().toTypedArray())
        val children = children.removeFirst().toTypedArray()

        if (namespace != null) {
            attributes["attributes"] = JSON.parse(JSON.stringify(attributes))
            attributes["namespace"] = namespace
        }
        val vnode = h(selector = element, properties = attributes, children = children)

        // add to parent
        _result = vnode
        this.children.firstOrNull()?.add(_result)
    }

    override fun writeAttribute(name: String, value: String) {
        attributes.first().add(name to value)
    }

    override fun writeText(text: String) {
        children.first().add(text)
    }

    override fun result(): VNode = _result!!
}
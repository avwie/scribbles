package nl.avwie.vdom

import kotlin.math.max

class Renderer<Msg, T>(private val target: Target<T>, private val dispatcher: Dispatcher<Msg>) {

    data class Mounted<Msg, T>(val element: T, val container: T?, val node: Node<Msg>, val childNodes: List<Mounted<Msg, T>>)

    interface Target<T> {
        fun createElement(name: String, namespace: String?): T
        fun setAttribute(element: T, key: String, value: String)
        fun setText(element: T, text: String?)
        fun appendChild(container: T?, child: T)
        fun remove(element: T)
        fun removeAttribute(element: T, key: String)
        fun setEventHandler(element: T, event: String, callback: () -> Unit)
        fun removeEventHandler(element: T, event: String)
    }

    private var root: Mounted<Msg, T>? = null
    val rootElement get() = root?.element

    fun render(node: Node<Msg>) {
        root = root?.let { updateNode(it, node) } ?: mount(node, null)
    }

    private fun updateNode(current: Mounted<Msg, T>, updated: Node<Msg>): Mounted<Msg, T> {
        return when {
            current.node == updated -> current
            current.node.name != updated.name -> replaceNode(current, updated)
            else -> updateNodeDetailed(current, updated)
        }
    }

    private fun replaceNode(current: Mounted<Msg, T>, updated: Node<Msg>): Mounted<Msg, T> {
        target.remove(current.element)
        return mount(updated, current.container)
    }

    private fun updateNodeDetailed(current: Mounted<Msg, T>, updated: Node<Msg>): Mounted<Msg, T> {
        return current
            .let { updateAttributes(it, updated)}
            .let { updateEvents(it, updated )}
            .let { updateText(it, updated) }
            .let { updateChildren(it, updated) }
    }

    private fun updateAttributes(current: Mounted<Msg, T>, updated: Node<Msg>): Mounted<Msg, T> = when (current.node.attributes) {
        updated.attributes -> current
        else -> {
            val leftKeys = current.node.attributes.keys.toSet()
            val rightKeys = updated.attributes.keys.toSet()

            val toRemove = leftKeys - rightKeys
            val toSet = rightKeys.filter { current.node.attributes[it] != updated.attributes[it] }

            toRemove.forEach { target.removeAttribute(current.element, it) }
            toSet.forEach { target.setAttribute(current.element, it, updated.attributes[it]!!) }
            current.copy(node = current.node.copy(attributes = updated.attributes))
        }
    }

    private fun updateEvents(current: Mounted<Msg, T>, updated: Node<Msg>): Mounted<Msg, T> = when (current.node.events) {
        updated.events -> current
        else -> {
            val leftKeys = current.node.events.keys.toSet()
            val rightKeys = updated.events.keys.toSet()

            val toRemove = leftKeys - rightKeys
            val toSet = rightKeys.filter { current.node.events[it] != updated.events[it] }

            toRemove.forEach { target.removeEventHandler(current.element, it) }
            toSet.forEach { target.setEventHandler(current.element, it) {
                    dispatcher.dispatch(updated.events[it]!!)
                }
            }
            current.copy(node = current.node.copy(events = updated.events))
        }
    }

    private fun updateText(current: Mounted<Msg, T>, updated: Node<Msg>): Mounted<Msg, T> = when (current.node.text) {
        updated.text -> current
        else -> {
            target.setText(current.element, updated.text)
            current.copy(node = current.node.copy(text = updated.text))
        }
    }

    private fun updateChildren(current: Mounted<Msg, T>, updated: Node<Msg>): Mounted<Msg, T> {
        val indices = (0 until max(current.childNodes.size, updated.childNodes.size))
        val mountedChildren = indices.mapNotNull { i ->
            val mountedChild = current.childNodes.getOrNull(i)
            val updatedChild = updated.childNodes.getOrNull(i)
            updateChild(current.container, mountedChild, updatedChild)
        }
        return current.copy(
            node = current.node.copy(
                childNodes = mountedChildren.map { it.node }
            ),
            childNodes = mountedChildren
        )
    }

    private fun updateChild(container: T?, mounted: Mounted<Msg, T>?, updated: Node<Msg>?): Mounted<Msg, T>? = when {
        mounted == null && updated != null -> mount(updated, container)
        mounted != null && updated == null -> {
            target.remove(mounted.element)
            null
        }
        mounted == null && updated == null -> null
        else -> updateNode(mounted!!, updated!!)
    }

    private fun mount(node: Node<Msg>, container: T?): Mounted<Msg, T> {
        val element = renderNodeWithoutChildren(node)
        val mountedChildren = node.childNodes.map { mount(it, element) }
        target.appendChild(container, element)
        return Mounted(element, container, node, mountedChildren)
    }

    private fun renderNodeWithoutChildren(node: Node<Msg>): T {
        val element = target.createElement(node.name, node.namespace)
        node.attributes.forEach { (key, value) ->
            target.setAttribute(element, key, value)
        }
        node.events.forEach { (event, msg) ->
            target.setEventHandler(element, event) {
                dispatcher.dispatch(msg)
            }
        }
        target.setText(element, node.text)
        return element
    }
}
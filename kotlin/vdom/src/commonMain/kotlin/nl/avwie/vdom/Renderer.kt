package nl.avwie.vdom

import kotlin.math.max

class Renderer<T>(private val target: Target<T>) {

    data class Mounted<T>(val element: T, val container: T?, val node: Node, val childNodes: List<Mounted<T>>)

    interface Target<T> {
        fun createElement(name: String, namespace: String?): T
        fun setAttribute(element: T, key: String, value: String)
        fun setText(element: T, text: String?)
        fun appendChild(container: T?, child: T)
        fun remove(element: T)
        fun removeAttribute(element: T, key: String)
    }

    private var root: Mounted<T>? = null
    val rootElement get() = root?.element

    fun render(node: Node) {
        root = root?.let { updateNode(it, node) } ?: mount(node, null)
    }

    private fun update(updated: Node): Mounted<T> {
        return updateNode(root!!, updated)
    }

    private fun updateNode(current: Mounted<T>, updated: Node): Mounted<T> {
        return when {
            current.node == updated -> current
            current.node.name != updated.name -> replaceNode(current, updated)
            else -> updateNodeDetailed(current, updated)
        }
    }

    private fun replaceNode(current: Mounted<T>, updated: Node): Mounted<T> {
        target.remove(current.element)
        return mount(updated, current.container)
    }

    private fun updateNodeDetailed(current: Mounted<T>, updated: Node): Mounted<T> {
        return current
            .let { updateAttributes(it, updated)}
            .let { updateText(it, updated) }
            .let { updateChildren(it, updated) }
    }

    private fun updateAttributes(current: Mounted<T>, updated: Node): Mounted<T> = when (current.node.attributes) {
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

    private fun updateText(current: Mounted<T>, updated: Node): Mounted<T> = when (current.node.text) {
        updated.text -> current
        else -> {
            target.setText(current.element, updated.text)
            current.copy(node = current.node.copy(text = updated.text))
        }
    }

    private fun updateChildren(current: Mounted<T>, updated: Node): Mounted<T> {
        val indices = (0 until max(current.childNodes.size, updated.childNodes.size))
        val mountedChildren = indices.mapNotNull { i ->
            val mountedChild = current.childNodes.getOrNull(i)
            val updatedChild = updated.childNodes.getOrNull(i)
            updateChild(current.container, mountedChild, updatedChild)
        }
        return current.copy(childNodes = mountedChildren)
    }

    private fun updateChild(container: T?, mounted: Mounted<T>?, updated: Node?): Mounted<T>? = when {
        mounted == null && updated != null -> mount(updated, container)
        mounted != null && updated == null -> {
            target.remove(mounted.element)
            null
        }
        mounted == null && updated == null -> null
        else -> updateNode(mounted!!, updated!!)
    }

    private fun mount(node: Node, container: T?): Mounted<T> {
        val element = renderNodeWithoutChildren(node)
        val mountedChildren = node.childNodes.map { mount(it, element) }
        target.appendChild(container, element)
        return Mounted(element, container, node, mountedChildren)
    }

    private fun renderNodeWithoutChildren(node: Node): T {
        val element = target.createElement(node.name, node.namespace)
        node.attributes.forEach { (key, value) ->
            target.setAttribute(element, key, value)
        }
        target.setText(element, node.text)
        return element
    }
}
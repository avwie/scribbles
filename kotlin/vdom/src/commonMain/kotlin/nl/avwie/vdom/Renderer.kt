package nl.avwie.vdom

import kotlin.math.max

class Renderer(private val target: Target) {
    interface Target {
        fun down()
        fun up()
        fun next()
        fun reset()
        fun clear()

        fun createElement(node: Node)
        fun removeElement()

        fun updateAttribute(name: String, value: String)
        fun removeAttribute(name: String)

        fun setText(name: String)
    }

    private var current: Node? = null

    fun render(node: Node) {
        target.reset()
        when (current) {
            null -> {
                target.clear()
                target.down()
                target.createElement(node)
            }
            else -> {
                target.down()
                updateNode(current!!, node)
            }
        }
        current = node
    }

    private fun updateNode(current: Node, updated: Node) {
        when {
            current == updated -> target.next()
            current.name != updated.name || current.namespace != updated.namespace -> replaceNode(updated)
            else -> updateNodeDetailed(current, updated)
        }
    }

    private fun replaceNode(updated: Node) {
        target.removeElement()
        target.createElement(updated)
        target.next()
    }

    private fun updateNodeDetailed(current: Node, updated: Node) {
        updateAttributes(current.attributes, updated.attributes)
        updateText(current.text, updated.text)
        updateChildren(current.childNodes, updated.childNodes)
    }

    private fun updateAttributes(current: Map<String, String>, updated: Map<String, String>) {
        val leftKeys = current.keys.toSet()
        val rightKeys = updated.keys.toSet()

        val toRemove = leftKeys - rightKeys
        val toSet = rightKeys.filter { current[it] != updated[it] }

        toRemove.forEach(target::removeAttribute)
        toSet.forEach { key -> target.updateAttribute(key, updated[key]!!) }
    }

    private fun updateText(current: String?, updated: String?) {
        if (updated != null && current != updated) target.setText(updated)
    }

    private fun updateChildren(current: List<Node>, updated: List<Node>) {
        target.down()
        val length = max(current.size, updated.size)
        (0 until length).forEach { index ->
            val (currentChild, updatedChild) = current.getOrNull(index) to updated.getOrNull(index)
            updateChild(currentChild, updatedChild)
        }
        target.up()
    }

    private fun updateChild(current: Node?, updated: Node?) {
        when {
            current == null && updated != null -> target.createElement(updated)
            current != null && updated == null -> target.removeElement()
            else -> {
                updateNode(current!!, updated!!)
            }
        }
    }
}
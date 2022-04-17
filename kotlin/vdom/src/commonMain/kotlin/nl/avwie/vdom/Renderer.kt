package nl.avwie.vdom

import nl.avwie.vdom.Node.Companion.PROTECTED_ATTR_NAMES
import nl.avwie.vdom.Node.Companion.TEXT_ATTR_NAME
import kotlin.math.max

class Renderer(val target: RenderTarget<*>) {

    private var current: Node? = null

    fun render(node: Node) {
        target.reset()
        when (current) {
            null -> {
                target.clear()
                renderNode(node)
            }
            else -> {
                target.next()
                updateNode(current!!, node)
            }
        }
        current = node
    }

    private fun renderNode(node: Node) {
        node.toDefinition().write(target)
    }

    private fun updateNode(current: Node, updated: Node) {
        when {
            current == updated -> target.next()
            current.tagName != updated.tagName -> replaceNode(updated)
            else -> updateNodeDetailed(current, updated)
        }
    }

    private fun replaceNode(updated: Node) {
        target.removeElement()
        updated.toDefinition().write(target)
    }

    private fun removeNode() {
        target.removeElement()
    }

    private fun updateNodeDetailed(current: Node, updated: Node) {
        updateAttributes(current.attributes, updated.attributes)
        updateText(current.attributes[TEXT_ATTR_NAME], updated.attributes[TEXT_ATTR_NAME])
        updateChildren(current.children, updated.children)
    }

    private fun updateAttributes(current: Map<String, String>, updated: Map<String, String>) {
        val leftKeys = current.keys.filter { !PROTECTED_ATTR_NAMES.contains(it) }.toSet()
        val rightKeys = updated.keys.filter { !PROTECTED_ATTR_NAMES.contains(it) }.toSet()

        val toRemove = leftKeys - rightKeys
        val toSet = rightKeys.filter { current[it] != updated[it] }

        toRemove.forEach(target::removeAttribute)
        toSet.forEach { key -> target.writeAttribute(key, updated[key]!!) }
    }

    private fun updateText(current: String?, updated: String?) {
        if (updated != null && current != updated) target.writeText(updated)
    }

    private fun updateChildren(current: List<Node>, updated: List<Node>) {
        val length = max(current.size, updated.size)
        (0 until length).forEach { index ->
            val (currentChild, updatedChild) = current.getOrNull(index) to updated.getOrNull(index)
            updateChild(currentChild, updatedChild)
        }
    }

    private fun updateChild(current: Node?, updated: Node?) {
        when {
            current == null && updated != null -> renderNode(updated)
            current != null && updated == null -> removeNode()
            else -> {
                target.next()
                updateNode(current!!, updated!!)
            }
        }
    }
}
package nl.avwie.vdom

import kotlin.math.max

object Differ {

    fun diff(left: VNode, right: VNode): List<Patch> =
        diffTagName(left, right)
            .plus(diffAttributes(left, right))
            .plus(diffChildren(left, right))

    private fun diffTagName(left: VNode, right: VNode): List<Patch> = when {
        left.tagName != right.tagName -> listOf(ChangeTagName(left, left.tagName, right.tagName))
        else -> listOf()
    }

    private fun diffAttributes(left: VNode, right: VNode): List<Patch> {
        val leftKeys = left.attributes.keys
        val rightKeys = right.attributes.keys

        val onlyLeft = leftKeys - rightKeys
        val onlyRight = rightKeys - leftKeys
        val both = onlyLeft.intersect(onlyRight)

        return onlyLeft.map { toRemove -> RemoveAttribute(left, toRemove) }
            .plus(onlyRight.map { toAdd -> AddAttribute(left, toAdd, right.attributes[toAdd]!!) })
            .plus(both.flatMap { key -> diffAttribute(left, right, key) })
    }

    private fun diffAttribute(left: VNode, right: VNode, key: String): List<Patch> = when {
        left.attributes[key] != right.attributes[key] -> listOf(ChangeAttribute(left, key, right.attributes[key]!!))
        else -> listOf()
    }

    private fun diffChildren(left: VNode, right: VNode): List<Patch> {
        val length = max(left.children.size, right.children.size)
        return (0 until length).flatMap { index ->
            diffChild(left, left.children.getOrNull(index), right.children.getOrNull(index))
        }
    }

    private fun diffChild(parent: VNode, left: VNode?, right: VNode?): List<Patch> = when {
        left != null && right == null -> listOf(RemoveVNode(parent, left))
        left == null && right != null -> listOf(AddVNode(parent, right))
        else -> diff(left!!, right!!)
    }
}
package nl.avwie.vdom

import kotlin.math.max

object Differ {

    fun diff(left: Node, right: Node): List<Patch> = when {
        left == right -> listOf(Skip)
        left.tagName != right.tagName -> listOf(Remove, Add(right))
        else -> listOf(
            Patches(diffAttributes(left.attributes, right.attributes), diffChildren(left.children, right.children))
        )
    }

    private fun diffAttributes(left: Map<String, String>, right: Map<String, String>): List<AttrPatch> {
        val leftKeys = left.keys
        val rightKeys = right.keys

        val toRemove = leftKeys - rightKeys
        val toSet = rightKeys.filter { left[it] != right[it] }
        return toRemove.map { RemoveAttr(it) } + toSet.map { SetAttr(it, right[it]!!) }
    }

    private fun diffChildren(left: List<Node>, right: List<Node>): List<Patch> {
        val length = max(left.size, right.size)
        return (0 until length).flatMap { index ->
            diffChild(left.getOrNull(index), right.getOrNull(index))
        }
    }

    private fun diffChild(left: Node?, right: Node?): List<Patch> = when {
        left != null && right == null -> listOf(Remove)
        left == null && right != null -> listOf(Add(right))
        else -> diff(left!!, right!!)
    }
}
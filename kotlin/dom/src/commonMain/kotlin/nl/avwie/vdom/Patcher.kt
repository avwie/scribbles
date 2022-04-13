package nl.avwie.vdom

object Patcher {

    fun patch(node: VNode, patches: List<Patch>): VNode {
        return patchAndConsume(node, patches).first
    }

    private fun patchAndConsume(node: VNode, patches: List<Patch>): Pair<VNode, List<Patch>> {
        if (patches.isEmpty()) return node to listOf()

        val applicable = patches.takeWhile { it.target == node }
        val remaining = patches.dropWhile { it.target == node }

        val updatedChildrenAndRemaining = node.children.fold(listOf<VNode>() to remaining) { (children, remaining), child ->
            patchAndConsume(child, remaining).let {
                (children + it.first) to it.second
            }
        }

        val nodeWithChildrenUpdated = node.copy(children = updatedChildrenAndRemaining.first)
        return applicable.fold(nodeWithChildrenUpdated) { n, p -> patch(n, p) } to updatedChildrenAndRemaining.second
    }

    private fun patch(node: VNode, patch: Patch): VNode = when (patch) {
        is ChangeTagName -> node.copy(tagName = patch.new)
        is AddAttribute -> node.copy(attributes = node.attributes + (patch.name to patch.value))
        is ChangeAttribute -> node.copy(attributes = node.attributes - patch.name + (patch.name to patch.newValue))
        is RemoveAttribute -> node.copy(attributes = node.attributes - patch.name)
        is AddVNode -> node.copy(children = node.children + patch.child)
        is RemoveVNode -> node.copy(children = node.children.filter { it != patch.child })
    }
}
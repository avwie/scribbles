package nl.avwie.vdom

sealed interface Patch {
    val target: VNode
}

data class ChangeTagName(override val target: VNode, val old: String, val new: String): Patch
data class AddAttribute(override val target: VNode, val name: String, val value: String): Patch
data class RemoveAttribute(override val target: VNode, val name: String): Patch
data class ChangeAttribute(override val target: VNode, val name: String, val newValue: String): Patch

data class AddVNode(override val target: VNode, val child: VNode): Patch
data class RemoveVNode(override val target: VNode, val child: VNode): Patch
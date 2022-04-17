package nl.avwie.vdom

import nl.avwie.dom.Definition
import nl.avwie.vdom.Node.Companion.NAMESPACE_ATTR_NAME
import nl.avwie.vdom.Node.Companion.PROTECTED_ATTR_NAMES
import nl.avwie.vdom.Node.Companion.TEXT_ATTR_NAME

data class Node(
    val tagName: String,
    val attributes: Map<String, String>,
    val children: List<Node>
) {
    companion object {
        const val NAMESPACE_ATTR_NAME = "__namespace"
        const val TEXT_ATTR_NAME = "__text"
        val PROTECTED_ATTR_NAMES = setOf(NAMESPACE_ATTR_NAME, TEXT_ATTR_NAME)
    }
}

fun Node.toDefinition(): Definition = Definition.build(if (attributes[NAMESPACE_ATTR_NAME].isNullOrBlank()) null else attributes[NAMESPACE_ATTR_NAME]) {
    val validAttributes = attributes.filterKeys { !PROTECTED_ATTR_NAMES.contains(it) }
    tagName.invoke(validAttributes) {
        attributes[TEXT_ATTR_NAME]?.also { text(it) }
        children.forEach { child ->
            include(child.toDefinition())
        }
    }
}

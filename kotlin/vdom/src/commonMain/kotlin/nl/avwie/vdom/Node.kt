package nl.avwie.vdom

import nl.avwie.dom.Definition

data class Node(
    val tagName: String,
    val attributes: Map<String, String>,
    val children: List<Node>
) {
    fun toDefinition(): Definition = Definition.build(attributes["namespace"]) {
        tagName.invoke(attributes.filter { (key, value) -> key != "namespace" || value.isNotEmpty() }) {
            children.forEach { child ->
                include(child.toDefinition())
            }
        }
    }
}

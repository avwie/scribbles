package nl.avwie.vdom

data class VNode(
    val tagName: String,
    val attributes: Map<String, String>,
    val children: List<VNode>
)

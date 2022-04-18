package nl.avwie.vdom

data class Node(
    val name: String,
    val attributes: Map<String, String>,
    val childNodes: List<Node>,
    val namespace: String?,
    val text: String?
) {
    class BuilderScope(private val name: String, var namespace: String? = null) {

        val attributes = mutableMapOf<String, String>()
        val children = mutableListOf<Node>()

        var text: String? = null

        fun build(): Node = Node(name, attributes, children, namespace, text)

        fun node(name: String, block: BuilderScope.() -> Unit = {}) {
            val builder = BuilderScope(name, namespace)
            block(builder)
            children.add(builder.build())
        }

        operator fun String.invoke(block: BuilderScope.() -> Unit = {}) = node(this, block)

        infix fun String.by(value: String) {
            attributes[this] = value
        }

        operator fun String.unaryPlus() {
            text = this
        }
    }
}

fun node(name: String, namespace: String? = null, block: Node.BuilderScope.() -> Unit): Node {
    val scope = Node.BuilderScope(name, namespace)
    block(scope)
    return scope.build()
}

fun html(block: Node.BuilderScope.() -> Unit) = node("html", "http://www.w3.org/1999/xhtml", block)
fun svg(block: Node.BuilderScope.() -> Unit) = node("svg", "http://www.w3.org/2000/svg") {
    attributes["version"] = "1.1"
    block()
}
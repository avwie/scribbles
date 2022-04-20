package nl.avwie.vdom

data class Node<Msg>(
    val name: String,
    val attributes: Map<String, String>,
    val events: Map<String, Msg>,
    val childNodes: List<Node<Msg>>,
    val namespace: String?,
    val text: String?
) {
    class BuilderScope<Msg>(private val name: String, var namespace: String? = null) {

        val attributes = mutableMapOf<String, String>()
        val events = mutableMapOf<String, Msg>()
        val children = mutableListOf<Node<Msg>>()
        var text: String? = null

        fun build(): Node<Msg> = Node(name, attributes, events, children, namespace, text)
        fun node(name: String, block: BuilderScope<Msg>.() -> Unit = {}) = node(name, namespace, block)
        fun node(name: String, namespace: String?, block: BuilderScope<Msg>.() -> Unit = {}) {
            val builder = BuilderScope<Msg>(name, namespace)
            block(builder)
            children.add(builder.build())
        }

        fun event(name: String, message: Msg) {
            events[name] = message
        }

        operator fun String.invoke(block: BuilderScope<Msg>.() -> Unit = {}) = node(this, block)

        infix fun String.by(value: String) {
            attributes[this] = value
        }

        operator fun String.unaryPlus() {
            text = this
        }
    }
}

fun <Msg> node(name: String, namespace: String? = null, block: Node.BuilderScope<Msg>.() -> Unit): Node<Msg> {
    val scope = Node.BuilderScope<Msg>(name, namespace)
    block(scope)
    return scope.build()
}

fun <Msg> html(block: Node.BuilderScope<Msg>.() -> Unit) = node("html", "http://www.w3.org/1999/xhtml", block)
fun <Msg> svg(block: Node.BuilderScope<Msg>.() -> Unit) = node<Msg>("svg", "http://www.w3.org/2000/svg") {
    attributes["version"] = "1.1"
    block()
}
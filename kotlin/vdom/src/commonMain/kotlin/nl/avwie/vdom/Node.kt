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

        fun build(): Node<Msg> = Node(name, attributes.toMap(), events.toMap(), children.toList(), namespace, text)
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

        infix fun String.by(value: Any) {
            attributes[this] = value.toString()
        }

        operator fun String.unaryPlus() {
            text = this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Node<*>

        if (name != other.name) return false
        if (namespace != other.namespace) return false
        if (text != other.text) return false
        if (!equalsInDetail(other)) return false

        return true
    }

    private fun equalsInDetail(other: Node<*>): Boolean {
        if (attributes != other.attributes) return false
        if (events != other.events) return false
        if (childNodes != other.childNodes) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + events.hashCode()
        result = 31 * result + childNodes.hashCode()
        result = 31 * result + (namespace?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        return result
    }


}

fun <Msg> node(name: String, namespace: String? = null, block: Node.BuilderScope<Msg>.() -> Unit): Node<Msg> {
    val scope = Node.BuilderScope<Msg>(name, namespace)
    block(scope)
    return scope.build()
}

fun <Msg> html(root: String = "html", block: Node.BuilderScope<Msg>.() -> Unit) = node(root, "http://www.w3.org/1999/xhtml", block)
fun <Msg> svg(root: String = "svg", block: Node.BuilderScope<Msg>.() -> Unit) = node<Msg>(root, "http://www.w3.org/2000/svg") {
    if (root == "svg") attributes["version"] = "1.1"
    block()
}
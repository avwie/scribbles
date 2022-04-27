package nl.avwie.vdom

data class Node<Msg>(
    val name: String,
    val attributes: Map<String, String>,
    val events: Map<String, Msg>,
    val childNodes: List<Node<Msg>>,
    val namespace: String?,
    val text: String?
) {
    class BuilderScope<Msg>(private val name: String, var namespace: String? = null, attrs: Array<out Pair<String, Any>>) {

        val attributes = attrs.map { (k, v) -> k to v.toString() }.toMap().toMutableMap()
        val events = mutableMapOf<String, Msg>()
        val children = mutableListOf<Node<Msg>>()
        var text: String? = null

        fun build(): Node<Msg> = Node(name, attributes.toMap(), events.toMap(), children.toList(), namespace, text)

        fun node(name: String, block: BuilderScope<Msg>.() -> Unit = {}) = node(
            name = name, namespace = namespace, attrs = arrayOf(), block = block
        )

        fun node(name: String, namespace: String?, block: BuilderScope<Msg>.() -> Unit = {})  = node(
            name = name, namespace = namespace, attrs = arrayOf(), block = block
        )

        fun node(name: String, vararg attrs: Pair<String, Any>, block: BuilderScope<Msg>.() -> Unit = {}) = node(
            name = name, namespace = namespace, attrs = attrs, block = block
        )

        fun node(name: String, namespace: String?,  vararg attrs: Pair<String, Any>, block: BuilderScope<Msg>.() -> Unit) {
            val builder = BuilderScope<Msg>(name, namespace, attrs)
            block(builder)
            children.add(builder.build())
        }

        fun event(name: String, message: Msg) {
            events[name] = message
        }

        fun attr(key: String, value: Any) {
            attributes[key] = value.toString()
        }

        fun attr(kv: Pair<String, Any>) = attr(kv.first, kv.second)

        operator fun String.invoke(block: BuilderScope<Msg>.() -> Unit = {}) = node(
            name = this, namespace = namespace, attrs = arrayOf(), block = block
        )

        operator fun String.invoke(vararg attrs: Pair<String, Any>, block: BuilderScope<Msg>.() -> Unit = {}) = node(
            name = this, namespace = namespace, attrs = attrs, block = block
        )

        infix fun String.by(value: Any): Pair<String, Any>  {
            return this to value
        }

        operator fun String.unaryPlus() {
            text = this
        }
    }
}

fun <Msg> node(name: String, namespace: String? = null, vararg attrs: Pair<String, Any> = arrayOf(), block: Node.BuilderScope<Msg>.() -> Unit): Node<Msg> {
    val scope = Node.BuilderScope<Msg>(name, namespace, attrs)
    block(scope)
    return scope.build()
}

fun <Msg> html(root: String = "html", vararg attrs: Pair<String, Any>, block: Node.BuilderScope<Msg>.() -> Unit) = node(
    name = root, namespace = "http://www.w3.org/1999/xhtml", attrs = attrs, block = block)

fun <Msg> svg(root: String = "svg", vararg attrs: Pair<String, Any> = arrayOf(), block: Node.BuilderScope<Msg>.() -> Unit) =
    node<Msg>(name = root, namespace = "http://www.w3.org/2000/svg", attrs = attrs) {
    if (root == "svg") attributes["version"] = "1.1"
    block()
}
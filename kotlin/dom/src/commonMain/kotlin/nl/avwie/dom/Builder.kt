package nl.avwie.dom

class Builder(
    private val writer: Writer<*>,
    private val namespace: String?
) {

    private fun element(name: String, block: Builder.() -> Unit = {}) {
        writer.beginElement(name, namespace)
        block(this)
        writer.endElement()
    }

    private fun attribute(name: String, value: String) {
        writer.writeAttribute(name, value)
    }

    fun text(raw: String) {
        writer.writeText(raw)
    }

    operator fun String.invoke(block: Builder.() -> Unit = {}) {
        element(this, block)
    }

    operator fun String.invoke(args: Map<String, String>, block: Builder.() -> Unit = {}) {
        element(this) {
            args.forEach { (name, value) -> attribute(name, value) }
            block(this)
        }
    }

    operator fun String.invoke(
        vararg args: Pair<String?, Any?>?,
        block: Builder.() -> Unit = {})
    {
        element(this) {
            args.filterNotNull()
                .filter { (name, value) -> name != null && value != null }
                .forEach { (name, value) -> attribute(name!!, value.toString()) }
            block(this)
        }
    }

    fun include(definition: Definition) {
        definition.write(writer)
    }
}
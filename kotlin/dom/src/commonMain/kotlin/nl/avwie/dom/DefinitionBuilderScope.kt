package nl.avwie.dom

class DefinitionBuilderScope(
    private val writer: Writer,
    private val namespace: String?
) {

    private fun element(name: String, block: DefinitionBuilderScope.() -> Unit = {}) {
        writer.beginElement(name, namespace)
        block(this)
        writer.endElement()
    }

    private fun attribute(name: String, value: String) {
        writer.writeAttribute(name, value, namespace)
    }

    private fun text(raw: String) {
        writer.writeText(raw)
    }

    operator fun String.invoke(block: DefinitionBuilderScope.() -> Unit = {}) {
        element(this, block)
    }

    operator fun String.invoke(
        vararg args: Pair<String?, Any?>?,
        block: DefinitionBuilderScope.() -> Unit = {})
    {
        element(this) {
            args.filterNotNull()
                .filter { (name, value) -> name != null && value != null }
                .forEach { (name, value) -> attribute(name!!, value.toString()!!) }
            block(this)
        }
    }
}
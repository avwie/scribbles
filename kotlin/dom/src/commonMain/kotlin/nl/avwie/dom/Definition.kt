package nl.avwie.dom

fun interface Definition {

    fun write(writer: Writer)

    companion object {
        fun build(
            namespace: String?,
            block: DefinitionBuilderScope.() -> Unit = {}
        ): Definition = Definition { writer ->
            block(DefinitionBuilderScope(writer, namespace))
        }

        fun build(
            block: DefinitionBuilderScope.() -> Unit
        ): Definition = build(null, block)
    }
}
package nl.avwie.dom

fun interface Definition {

    fun write(writer: Writer)

    companion object {
        fun build(
            namespace: String?,
            block: Builder.() -> Unit = {}
        ): Definition = Definition { writer ->
            block(Builder(writer, namespace))
        }
    }
}
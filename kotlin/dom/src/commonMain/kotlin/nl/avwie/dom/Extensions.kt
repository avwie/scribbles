package nl.avwie.dom

fun Definition.renderAsString(
    prettyPrint: Boolean = false,
    prettyPrintIndent: Int = 4
): String {
    return StringBuilder().also { builder ->
        this.write(AppendableWriter(builder, prettyPrint, prettyPrintIndent))
    }.toString()
}
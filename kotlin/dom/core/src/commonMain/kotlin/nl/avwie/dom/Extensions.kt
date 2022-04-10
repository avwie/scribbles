package nl.avwie.dom

fun Definition.renderAsString(
    prettyPrint: Boolean = false,
    prettyPrintIndent: Int = 4
): String {
    return AppendableWriter(StringBuilder(), prettyPrint, prettyPrintIndent).result()
}
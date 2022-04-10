package nl.avwie.dom

class AppendableWriter(
    private val appendable: Appendable,
    private val prettyPrint: Boolean,
    private val prettyPrintIndent: Int
) : Writer<String> {
    private val tags = ArrayDeque<String>()
    private val hasChildren = ArrayDeque<Boolean>()
    private var level = 0
    private val indent = if (prettyPrint) " ".repeat(prettyPrintIndent) else ""
    private val newline = if (prettyPrint) "\n" else ""

    override fun beginElement(tag: String, namespace: String?) {
        if (!hasChildren.isEmpty()) {
            val currentHasChildren = hasChildren.removeFirst()
            if (!currentHasChildren) {
                appendable.append(">$newline")
            }
            hasChildren.addFirst(true)
        }

        level += 1
        appendable.append("${indent()}<$tag")
        tags.addFirst(tag)
        hasChildren.addFirst(false)
    }

    override fun endElement() {
        val tag = tags.removeFirst()
        when (hasChildren.removeFirst()) {
            true -> appendable.append("${indent()}</$tag>$newline")
            else -> appendable.append(" />$newline")
        }
        level -= 1
    }

    override fun writeAttribute(name: String, value: String) {
        appendable.append(" $name=\"$value\"")
    }

    override fun result(): String = appendable.toString()

    override fun writeText(text: String) {
        appendable.append(text)
    }

    private fun indent(): String = indent.repeat(level - 1)
}
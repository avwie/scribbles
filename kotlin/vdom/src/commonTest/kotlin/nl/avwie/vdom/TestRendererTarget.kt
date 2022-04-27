package nl.avwie.vdom

class TestRendererTarget : Renderer.Target<TestRendererTarget.Element> {

    var root: Element? = null

    data class Element(
        val name: String,
        var text: String?,
        var parent: Element?,
        val attributes: MutableMap<String, String>,
        val events: MutableSet<String>,
        val childNodes: MutableList<Element>
    ) {
        override fun toString(): String {
            return with(StringBuilder()) {
                append(name)
                if (attributes.isNotEmpty()) {
                    append(" (")
                    append((attributes.toMap() + mapOf("text" to (text ?: ""))).map { (k, v) -> "$k => $v" }.joinToString(", "))
                    append(")")
                }

                if (childNodes.isNotEmpty()) {
                    appendLine(" {")
                    childNodes.map { it.toString().lines() }.forEach { line ->
                        appendLine("\t$line")
                    }
                    appendLine("}")
                }

                toString()
            }
        }
    }

    override fun createElement(name: String, namespace: String?): Element {
        return Element(name, null, null, mutableMapOf(), mutableSetOf(), mutableListOf())
    }

    override fun setAttribute(element: Element, key: String, value: String) {
        element.attributes[key] = value
    }

    override fun setText(element: Element, text: String?) {
        element.text = text
    }

    override fun appendChild(container: Element?, child: Element) {
        child.parent = container
        container?.childNodes?.add(child)

        if (container == null) root = child
    }

    override fun remove(element: Element) {
        element.parent?.childNodes?.remove(element)
    }

    override fun removeAttribute(element: Element, key: String) {
        element.attributes.remove(key)
    }

    override fun setEventHandler(element: Element, event: String, callback: () -> Unit) {
        element.events.add(event)
    }

    override fun removeEventHandler(element: Element, event: String) {
        element.events.remove(event)
    }
}
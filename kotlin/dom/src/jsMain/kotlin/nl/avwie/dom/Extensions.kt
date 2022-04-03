package nl.avwie.dom

import org.w3c.dom.Element

fun Definition.toElement(): Element = BrowserWriter().also { writer ->
    this.write(writer)
}.result!!
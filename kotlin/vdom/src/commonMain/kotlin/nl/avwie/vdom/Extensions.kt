package nl.avwie.vdom

import nl.avwie.dom.Definition

fun Definition.toNode(): Node {
    return NodeWriter().also { this.write(it) }.result()
}
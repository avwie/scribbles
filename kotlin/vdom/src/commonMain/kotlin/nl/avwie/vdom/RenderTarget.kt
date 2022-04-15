package nl.avwie.vdom

import nl.avwie.dom.Writer

interface RenderTarget<T> : Writer<T> {
    fun reset();
    fun removeElement();
    fun skip();

    fun removeAttribute(name: String)
}
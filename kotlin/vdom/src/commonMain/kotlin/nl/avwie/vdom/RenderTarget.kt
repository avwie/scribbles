package nl.avwie.vdom

import nl.avwie.dom.Writer

interface RenderTarget<T> : Writer<T> {
    fun reset();
    fun removeElement();
    fun next();

    fun removeAttribute(name: String)
}
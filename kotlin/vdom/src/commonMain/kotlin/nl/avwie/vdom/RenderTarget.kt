package nl.avwie.vdom

import nl.avwie.dom.Writer

interface RenderTarget<T> : Writer<T> {
    fun clear();
    fun reset();
    fun next();

    fun removeAttribute(name: String)
    fun removeElement();
}
package nl.avwie.dom

interface Writer {
    fun beginElement(name: String, namespace: String?)
    fun endElement()

    fun writeAttribute(name: String, value: String)
    fun writeText(text: String)
}
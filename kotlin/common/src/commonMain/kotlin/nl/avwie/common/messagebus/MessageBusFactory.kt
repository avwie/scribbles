package nl.avwie.common.messagebus

fun interface MessageBusFactory<T> {
    fun create(topic: String): MessageBus<T>
}
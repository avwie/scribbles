package nl.avwie.common.messagebus

interface SendBus<T> {
    suspend fun send(item: T)
}
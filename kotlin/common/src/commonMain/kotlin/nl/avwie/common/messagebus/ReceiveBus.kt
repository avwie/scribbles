package nl.avwie.common.messagebus

import kotlinx.coroutines.flow.SharedFlow

interface ReceiveBus<T> {
    val messages: SharedFlow<T>
}
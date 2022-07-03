package nl.avwie.common.messagebus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.w3c.dom.BroadcastChannel

fun BroadcastChannel.asMessageBus(
    scope: CoroutineScope
): MessageBus<String> = object : MessageBus<String> {

    private val _messages = MutableSharedFlow<String>()
    override val messages: SharedFlow<String> = _messages

    init {
        this@asMessageBus.onmessage = { event ->
            (event.data as? String)?.also { data ->
                scope.launch {
                    _messages.emit(data)
                }
            }
        }
    }

    override suspend fun send(item: String) {
        this@asMessageBus.postMessage(item)
    }
}
package nl.avwie.common.messagebus

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.w3c.fetch.RequestInit


class ServerSentEventBus(
    val publishEndpoint: String,
    val eventSource: EventSource,
    scope: CoroutineScope
) : MessageBus<String> {

    private val _messages = MutableSharedFlow<String>()
    override val messages: SharedFlow<String> = _messages

    init {
        eventSource.onmessage = { event ->
            (event.data as? String)?.also { data ->
                scope.launch {
                    _messages.emit(data)
                }
            }
        }
    }

    override suspend fun send(item: String) {
        window.fetch(
            publishEndpoint,
            RequestInit(
                method = "POST",
                body = item
            )
        ).await()
    }
}

fun ServerSentEventBus(
    publishEndpoint: String,
    subscribeEndpoint: String,
    scope: CoroutineScope
) = ServerSentEventBus(
    publishEndpoint,
    EventSource(subscribeEndpoint, EventSourceInit(withCredentials = false)),
    scope
)
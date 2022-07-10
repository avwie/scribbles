package nl.avwie.common.messagebus

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import nl.avwie.common.UUID
import nl.avwie.common.UUIDFactory
import nl.avwie.common.uuid
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.w3c.fetch.RequestInit


class ServerSentEventBus(
    val publishEndpoint: String,
    val eventSource: EventSource,
    scope: CoroutineScope,
    val clientId: UUID
) : MessageBus<String> {

    private val _messages = MutableSharedFlow<String>()
    override val messages: SharedFlow<String> = _messages

    init {
        eventSource.onmessage = { event ->
            (event.data as? String)?.also { data ->
                val senderId = UUIDFactory.fromString(data.substring(0, 36))
                if (senderId != clientId) {
                    scope.launch {
                        _messages.emit(data.substring(37))
                    }
                }
            }
        }
    }

    override suspend fun send(item: String) {
        val payload = "$clientId:$item"
        window.fetch(
            publishEndpoint,
            RequestInit(
                method = "POST",
                body = payload
            )
        ).await()
    }
}

fun ServerSentEventBus(
    publishEndpoint: String,
    subscribeEndpoint: String,
    scope: CoroutineScope,
    clientId: UUID = uuid()
) = ServerSentEventBus(
    publishEndpoint,
    EventSource(subscribeEndpoint, EventSourceInit(withCredentials = false)),
    scope,
    clientId
)
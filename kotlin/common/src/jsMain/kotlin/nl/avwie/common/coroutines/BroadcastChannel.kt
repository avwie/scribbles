package nl.avwie.common.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import org.w3c.dom.BroadcastChannel

inline fun <reified T> DistributableMutableSharedFlow<T>.broadcast(
    channel: BroadcastChannel,
    scope: CoroutineScope,
    serializersModule: SerializersModule = EmptySerializersModule
) {
    val jsonSerializer = Json {
        this.serializersModule = serializersModule
    }

    channel.onmessage = { event ->
        val deserialized = jsonSerializer.decodeFromString<DistributedMessage<T>>(event.data as String)
        if (deserialized.clientId != this.clientId) {
            scope.launch {
                this@broadcast.emit(deserialized.contents)
            }
        }
    }

    this.onEach { message ->
        val serialized = jsonSerializer.encodeToString(DistributedMessage(clientId = this.clientId, message))
        channel.postMessage(serialized)
    }.launchIn(scope)
}
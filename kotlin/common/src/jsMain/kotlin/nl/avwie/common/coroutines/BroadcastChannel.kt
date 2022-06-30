package nl.avwie.common.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import org.w3c.dom.BroadcastChannel

inline fun <reified T> BroadcastChannel.asMutableSharedFlow(
    scope: CoroutineScope,
    serializersModule: SerializersModule = EmptySerializersModule
): MutableSharedFlow<T> {
    val flow = MutableSharedFlow<T>()
    val distributed = flow.distribute(scope)

    val jsonSerializer = Json {
        this.serializersModule = serializersModule
    }

    distributed.onEach { message ->
        val serialized = jsonSerializer.encodeToString(message)
        this.postMessage(serialized)
    }.launchIn(scope)

    this.onmessage = { message ->
        val deserialized = jsonSerializer.decodeFromString<Distributed<T>>(message.data as String)
        scope.launch {
            distributed.emit(deserialized)
        }
    }

    return flow
}
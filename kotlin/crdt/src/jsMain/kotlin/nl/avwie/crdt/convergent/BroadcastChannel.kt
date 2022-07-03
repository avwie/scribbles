package nl.avwie.crdt.convergent

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

inline fun <reified T : Mergeable<T>> MergeableStateFlow<T>.broadcast(
    channel: BroadcastChannel,
    scope: CoroutineScope,
    serializersModule: SerializersModule = EmptySerializersModule,
) {
    val serializer = Json {
        this.serializersModule = serializersModule
    }

    this.onEach { update ->
        println("Sending ${serializer.encodeToString(update)}")
        channel.postMessage(serializer.encodeToString(update))
    }.launchIn(scope)

    channel.onmessage = { event ->
        val deserialized = serializer.decodeFromString<T>(event.data as String)
        println("Receiving: $deserialized")
        scope.launch {
            val result = this@broadcast.merge(deserialized)
            if (deserialized != result) {
                println("Sending ${serializer.encodeToString(result)}")
                channel.postMessage(serializer.encodeToString(result))
            }
        }
    }

    // initial publish
    channel.postMessage(serializer.encodeToString(value))
}
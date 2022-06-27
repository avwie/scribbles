package nl.avwie.common.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

inline fun <reified T> MutableSharedFlow<T>.serializeWith(
    backend: MutableSharedFlow<String>,
    serializersModule: SerializersModule = EmptySerializersModule,
    scope: CoroutineScope
) {
    val jsonSerializer = Json {
        this.serializersModule = serializersModule
    }

    this.onEach { value ->
        backend.emit(jsonSerializer.encodeToString(value))
    }.launchIn(scope)

    backend.onEach { string ->
        this.emit(jsonSerializer.decodeFromString(string))
    }
}

inline fun <reified T> MutableSharedFlow<T>.serializeWith(
        serializersModule: SerializersModule = EmptySerializersModule,
        scope: CoroutineScope
): MutableSharedFlow<String> {
    val backend = MutableSharedFlow<String>()
    this.serializeWith(backend, serializersModule, scope)
    return backend
}
package nl.avwie.common.messagebus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

private class DelegatingMessageBus<T>(
    val sendBus: SendBus<T>,
    val receiveBus: ReceiveBus<T>
) : MessageBus<T>, SendBus<T> by sendBus, ReceiveBus<T> by receiveBus

private class TransformingMessageBus<T, U>(
    val target: MessageBus<T>,
    scope: CoroutineScope,
    val fromTransform: (T) -> U,
    val toTransform: (U) -> T
) : MessageBus<U> {
    override val messages: SharedFlow<U> = target.messages
        .map { fromTransform(it) }
        .shareIn(scope, SharingStarted.Eagerly)

    override suspend fun send(item: U) {
        target.send(toTransform(item))
    }
}

fun <T, U> ReceiveBus<T>.sendInto(target: SendBus<U>, scope: CoroutineScope, transform: (T) -> U) {
    this.messages
        .onEach { target.send(transform(it)) }
        .launchIn(scope)
}

fun <T, U> SendBus<T>.receiveFrom(source: ReceiveBus<U>, scope: CoroutineScope, transform: (U) -> T) {
    source.sendInto(this, scope, transform)
}

fun <T, U> MessageBus<T>.transformInto(
    target: MessageBus<U>,
    scope: CoroutineScope,
    fromTransform: (T) -> U,
    toTransform: (U) -> T
) {
    this.sendInto(target, scope, fromTransform)
    this.receiveFrom(target, scope, toTransform)
}

fun <T, U> MessageBus<T>.transform(
    scope: CoroutineScope,
    fromTransform: (T) -> U,
    toTransform: (U) -> T
): MessageBus<U> = TransformingMessageBus(this, scope, fromTransform, toTransform)

inline fun <reified T> MessageBus<T>.serialize(
    scope: CoroutineScope,
    serializersModule: SerializersModule = EmptySerializersModule
): MessageBus<String> {
    val serializer = Json {
        this.serializersModule = serializersModule
    }

    return this.transform(
        scope = scope,
        fromTransform = { serializer.encodeToString(it) },
        toTransform = { serializer.decodeFromString(it) }
    )
}

inline fun <reified T> MessageBus<String>.deserialize(
    scope: CoroutineScope,
    serializersModule: SerializersModule = EmptySerializersModule
): MessageBus<T> {
    val serializer = Json {
        this.serializersModule = serializersModule
    }

    return this.transform(
        scope = scope,
        fromTransform = { serializer.decodeFromString(it) },
        toTransform = { serializer.encodeToString(it) }
    )
}

inline fun <reified T> MessageBus<T>.serializeInto(
    target: MessageBus<String>,
    scope: CoroutineScope,
    serializersModule: SerializersModule = EmptySerializersModule
) {
    val serializer = Json {
        this.serializersModule = serializersModule
    }

    return this.transformInto(
        target = target,
        scope = scope,
        fromTransform = { serializer.encodeToString(it) },
        toTransform = { serializer.decodeFromString(it) }
    )
}

inline fun <reified T> MessageBus<String>.deserializeInto(
    target: MessageBus<T>,
    scope: CoroutineScope,
    serializersModule: SerializersModule = EmptySerializersModule
) {
    val serializer = Json {
        this.serializersModule = serializersModule
    }

    return this.transformInto(
        target = target,
        scope = scope,
        fromTransform = { serializer.decodeFromString(it) },
        toTransform = { serializer.encodeToString(it) }
    )
}
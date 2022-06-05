package nl.avwie.common.messagebus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.typeOf

class SerializingMessageBus<T>(
    val sendBus: SerializingSendBus<T>,
    val receiveBus: SerializingReceiveBus<T>
) : MessageBus<T>, SendBus<T> by sendBus, ReceiveBus<T> by receiveBus {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        inline operator fun <reified T> invoke(
            backend: MessageBus<String>,
            serializerModule: SerializersModule = EmptySerializersModule,
            scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
        ): SerializingMessageBus<T> = SerializingMessageBus(
            SerializingSendBus(backend, typeOf<T>(), serializerModule),
            SerializingReceiveBus(backend, typeOf<T>(), serializerModule, scope)
        )
    }
}
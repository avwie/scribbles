package common.messagebus

import common.persistence.KeyValueStore
import common.persistence.SerializingKeyValueStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class SerializingReceiveBus<T>(
    private val backend: ReceiveBus<String>,
    type: KType,
    private val serializerModule: SerializersModule,
    private val scope: CoroutineScope
): ReceiveBus<T> {

    override val messages: SharedFlow<T> = backend.messages
        .map(::deserialize)
        .shareIn(scope, SharingStarted.Eagerly)

    private val jsonSerializer = Json {
        this.serializersModule = this@SerializingReceiveBus.serializerModule
    }

    @Suppress("UNCHECKED_CAST")
    private val serializer: KSerializer<T> = serializerModule.serializer(type) as KSerializer<T>

    private fun deserialize(input: String): T {
        return jsonSerializer.decodeFromString(serializer, input)
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        inline operator fun <reified T> invoke(
            backend: ReceiveBus<String>,
            serializerModule: SerializersModule = EmptySerializersModule,
            scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
        ): SerializingReceiveBus<T> = SerializingReceiveBus(backend, typeOf<T>(), serializerModule, scope)
    }
}
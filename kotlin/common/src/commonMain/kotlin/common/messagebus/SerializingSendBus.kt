package common.messagebus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class SerializingSendBus<T>(
    private val backend: SendBus<String>,
    type: KType,
    private val serializerModule: SerializersModule
): SendBus<T> {

    private val jsonSerializer = Json {
        this.serializersModule = this@SerializingSendBus.serializerModule
    }

    @Suppress("UNCHECKED_CAST")
    private val serializer: KSerializer<T> = serializerModule.serializer(type) as KSerializer<T>

    private fun serialize(input: T): String {
        return jsonSerializer.encodeToString(serializer, input)
    }

    override suspend fun send(item: T) {
        backend.send(serialize(item))
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        inline operator fun <reified T> invoke(
            backend: SendBus<String>,
            serializerModule: SerializersModule = EmptySerializersModule,
        ): SerializingSendBus<T> = SerializingSendBus(backend, typeOf<T>(), serializerModule)
    }
}
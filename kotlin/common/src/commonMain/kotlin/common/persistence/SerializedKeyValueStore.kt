package common.persistence

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class SerializedKeyValueStore<T>(
    private val backend: KeyValueStore<String>,
    type: KType,
    private val serializerModule: SerializersModule,
): KeyValueStore<T> {

    private val jsonSerializer = Json {
        this.serializersModule = this@SerializedKeyValueStore.serializerModule
    }

    @Suppress("UNCHECKED_CAST")
    private val serializer: KSerializer<T> = serializerModule.serializer(type) as KSerializer<T>

    override fun store(key: String, item: T) {
        backend.store(key, serialize(item))
    }

    override fun retrieve(key: String): T? {
        return backend.retrieve(key)?.let { deserialize(it) }
    }

    private fun serialize(input: T): String {
        return jsonSerializer.encodeToString(serializer, input)
    }

    private fun deserialize(input: String): T {
        return jsonSerializer.decodeFromString(serializer, input)
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        inline operator fun <reified T> invoke(
            backend: KeyValueStore<String>,
            serializerModule: SerializersModule = EmptySerializersModule,
        ): SerializedKeyValueStore<T> = SerializedKeyValueStore(backend, typeOf<T>(), serializerModule)
    }
}
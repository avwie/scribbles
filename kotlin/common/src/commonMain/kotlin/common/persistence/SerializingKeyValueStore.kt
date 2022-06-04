package common.persistence

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class SerializingKeyValueStore<T>(
    private val backend: KeyValueStore<String>,
    type: KType,
    private val serializerModule: SerializersModule
): KeyValueStore<T> {

    private val jsonSerializer = Json {
        this.serializersModule = this@SerializingKeyValueStore.serializerModule
    }

    @Suppress("UNCHECKED_CAST")
    private val serializer: KSerializer<T> = serializerModule.serializer(type) as KSerializer<T>

    override suspend fun contains(key: String): Boolean {
        return backend.get(key) != null
    }

    override suspend fun set(key: String, item: T) {
        backend.set(key, serialize(item))
    }

    override suspend fun get(key: String): T? {
        return backend.get(key)?.let { deserialize(it) }
    }

    override suspend fun remove(key: String) {
        backend.remove(key)
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
            serializerModule: SerializersModule = EmptySerializersModule
        ): SerializingKeyValueStore<T> = SerializingKeyValueStore(backend, typeOf<T>(), serializerModule)
    }
}
package common.persistence

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
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class SerializedKeyValueStore<T>(
    private val backend: KeyValueStore<String>,
    type: KType,
    private val serializerModule: SerializersModule,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
): KeyValueStore<T> {

    private val jsonSerializer = Json {
        this.serializersModule = this@SerializedKeyValueStore.serializerModule
    }

    override val updates: SharedFlow<KeyValueStore.Update<T>> = backend.updates
        .map { update ->
            KeyValueStore.Update(
                update.key,
                update.oldValue?.let(::deserialize),
                deserialize(update.newValue)
            )
        }.shareIn(scope, started = SharingStarted.Eagerly)


    @Suppress("UNCHECKED_CAST")
    private val serializer: KSerializer<T> = serializerModule.serializer(type) as KSerializer<T>

    override suspend fun store(key: String, item: T) {
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
            scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
        ): SerializedKeyValueStore<T> = SerializedKeyValueStore(backend, typeOf<T>(), serializerModule, scope)
    }
}
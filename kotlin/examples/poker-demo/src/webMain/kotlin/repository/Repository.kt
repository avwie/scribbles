package repository

import kotlinx.browser.window
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import org.w3c.dom.Storage
import org.w3c.dom.get
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface Repository<T> {
    fun retrieve(): T?
    fun store(item: T)
}

class LocalStorageRepository<T>(
    val key: String,
    type: KType,
    private val serializerModule: SerializersModule,
    private val onStorageUpdate: (T) -> Unit,
): Repository<T> {

    private val jsonSerializer = Json {
        this.serializersModule = this@LocalStorageRepository.serializerModule
    }

    @Suppress("UNCHECKED_CAST")
    private val serializer: KSerializer<T> = serializerModule.serializer(type) as KSerializer<T>

    private val storage: Storage = window.localStorage

    init {
        window.onstorage = { event ->
            if (event.key == key) {
                event.newValue?.let(::deserialize)?.also(onStorageUpdate)
            }
        }
    }

    override fun retrieve(): T? {
        val json = storage.getItem(key) ?: return null
        return deserialize(json)
    }

    override fun store(item: T) {
        val json = serialize(item)
        storage.setItem(key, json)
    }

    private fun serialize(input: T): String {
        jsonSerializer.encodeToString("")
        return jsonSerializer.encodeToString(serializer, input)
    }

    private fun deserialize(input: String): T {
        return jsonSerializer.decodeFromString(serializer, input)
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        inline operator fun <reified T> invoke(
            key: String,
            serializerModule: SerializersModule = EmptySerializersModule,
            noinline onStorageUpdate: (T) -> Unit = {}
        ): LocalStorageRepository<T> = LocalStorageRepository(key, typeOf<T>(), serializerModule, onStorageUpdate)
    }
}
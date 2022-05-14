package nl.avwie.crdt

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlin.reflect.KProperty

@kotlinx.serialization.Serializable(with=LWWCellSerializer::class)
class LWWCell<T>(initialValue: T, initialTimestamp: Instant? = null): Mergeable<LWWCell<T>> {

    var timestamp: Instant = initialTimestamp ?: Clock.System.now()
        private set

    var value: T = initialValue
        private set(value) {
            field = value
            timestamp = Clock.System.now()
        }

    override fun merge(other: LWWCell<T>): LWWCell<T> = when {
        timestamp < other.timestamp -> other
        timestamp > other.timestamp -> this
        else -> this // TODO: how to break ties?
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> T.toLWWCell() = LWWCell(this)

class LWWCellSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<LWWCell<T>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LWWCell") {
        element("timestamp", Instant.serializer().descriptor)
        element("value", dataSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: LWWCell<T>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, Instant.serializer(), value.timestamp)
            encodeSerializableElement(descriptor, 1, dataSerializer, value.value)
        }
    }

    override fun deserialize(decoder: Decoder): LWWCell<T> =
        decoder.decodeStructure(descriptor) {
            var timestamp: Instant? = null
            var value: T? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> timestamp = decodeSerializableElement(Instant.serializer().descriptor, index, Instant.serializer())
                    1 -> value = decodeSerializableElement(dataSerializer.descriptor, index, dataSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            LWWCell(value!!, timestamp!!)
        }
}
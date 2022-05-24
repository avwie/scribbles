package common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@kotlinx.serialization.Serializable(UUIDAsStringSerializer::class)
data class UUID(private val bytes: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UUID

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String {
        val appendable = StringBuilder()
        bytes.forEachIndexed { i, b ->
            if (i in dashLocations) appendable.append('-')
            appendable.append(b.toUByte().toString(16).padStart(2, '0'))
        }
        return appendable.toString()
    }

    companion object {
        private val dashLocations = setOf(4, 6, 8, 10)
        fun random() = UUIDFactory.random()
    }
}

object UUIDAsStringSerializer : KSerializer<UUID> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("uuid", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUIDFactory.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

expect object UUIDFactory {
    fun random(): UUID
    fun fromString(str: String): UUID
}

fun uuid(): UUID {
    return UUIDFactory.random()
}

fun uuid(str: String): UUID {
    return UUIDFactory.fromString(str)
}
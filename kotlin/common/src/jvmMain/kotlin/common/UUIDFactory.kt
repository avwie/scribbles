package common

import java.nio.ByteBuffer

actual object UUIDFactory {

    private fun toByteArray(uuid: java.util.UUID): ByteArray {
        val b = ByteBuffer.wrap(ByteArray(16))
        b.putLong(uuid.mostSignificantBits)
        b.putLong(uuid.leastSignificantBits)
        return b.array()
    }

    actual fun random(): UUID {
        return UUID(toByteArray(java.util.UUID.randomUUID()))
    }

    actual fun fromString(str: String): UUID {
        return UUID(toByteArray(java.util.UUID.fromString(str)))
    }
}
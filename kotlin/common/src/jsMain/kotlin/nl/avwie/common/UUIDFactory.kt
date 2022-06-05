package nl.avwie.common

import org.khronos.webgl.get

actual object UUIDFactory {
    actual fun random(): UUID {
        return UUID(UUIDAdapter.v4(null, ByteArray(16)))
    }

    actual fun fromString(str: String): UUID {
        val arr = UUIDAdapter.parse(str)
        val buf = ByteArray(16)
        (0 until arr.length).forEach { i ->
            buf[i] = arr[i]
        }
        return UUID(buf)
    }
}
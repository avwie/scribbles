import org.khronos.webgl.Uint8Array

@JsModule("uuid")
@JsNonModule
external object UUIDAdapter {
    fun v4(options: dynamic, buffer: ByteArray): ByteArray
    fun parse(str: String): Uint8Array
}
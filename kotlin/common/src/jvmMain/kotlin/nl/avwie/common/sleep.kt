package nl.avwie.common

actual fun sleep(ms: Long) {
    Thread.sleep(ms)
}
package common

import kotlin.js.Date

actual fun sleep(ms: Long) {

    val now = Date.now()
    var current = Date.now()
    while (current - now < ms) {
        current = Date.now()
    }
}
package nl.avwie.crdt

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.reflect.KProperty

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

fun <T> LWWOf(value: T) = LWWCell(value)
fun <T> T.toLLWCell() = LWWCell(this)
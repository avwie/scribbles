package nl.avwie.crdt.immutable

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import nl.avwie.crdt.Mergeable
import kotlin.reflect.KProperty

@kotlinx.serialization.Serializable
data class LWWValue<T>(val value: T, val timestamp: Instant) : Mergeable<LWWValue<T>> {
    constructor(value: T): this(value, Clock.System.now())

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun merge(other: LWWValue<T>): LWWValue<T> = when {
        timestamp < other.timestamp -> other
        timestamp > other.timestamp -> this
        else -> this
    }

    fun update(value: T) = LWWValue(value)
}

fun <T> LWWValueOf(value: T) = LWWValue(value)
fun <T> T.toLWWValue() = LWWValueOf(this)
package nl.avwie.crdt.convergent

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.reflect.KProperty

@kotlinx.serialization.Serializable
class MergeableValue<T>(
    val value: T,
    val timestamp: Instant,
    val discriminant: Int = Random.nextInt(),
) : Mergeable<MergeableValue<T>> {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun merge(other: MergeableValue<T>): MergeableValue<T> = when {
        timestamp < other.timestamp -> other
        timestamp > other.timestamp -> this

        // breaking ties based on discriminant
        discriminant <  other.discriminant -> other
        else -> this
    }

    override fun toString(): String {
        return "$value [${timestamp}]"
    }

    /**
     * If they are equal in value it doesn't really matter what the timestamp is
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MergeableValue<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}

fun <T> mergeableValueOf(value: T) = MergeableValue(value, Clock.System.now())
fun <T> mergeableDistantPastValueOf(value: T) = MergeableValue(value, Instant.DISTANT_PAST)
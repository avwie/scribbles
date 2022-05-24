package nl.avwie.crdt.immutable

import nl.avwie.crdt.Mergeable
import nl.avwie.crdt.TombstoneResolver

@kotlinx.serialization.Serializable
data class TombstoneSet<T, K>(
    val elements: Set<T>,
    val tombstones: Set<K>,
    val tombstoneResolver: TombstoneResolver<T, K>? = null
): Set<T> by elements, Mergeable<TombstoneSet<T, K>> {

    fun add(element: T): TombstoneSet<T, K> = when {
        tombstones.contains(tombstone(element)) -> this
        else -> copy(elements = elements + element)
    }

    fun addAll(elements: Iterable<T>): TombstoneSet<T, K> = copy(
        elements = this.elements + elements.filterNot { tombstones.contains(tombstone(it)) }
    )

    fun update(element: T, updater: (T) -> T) = when {
        elements.contains(element) -> copy(elements = elements - element + updater(element))
        else -> this
    }

    fun remove(element: T): TombstoneSet<T, K> = when {
        elements.contains(element) -> copy(
            elements = elements - elements,
            tombstones = tombstones + tombstone(element)
        )
        else -> this
    }

    fun removeAll(elements: Iterable<T>): TombstoneSet<T, K> = elements.filter { this.elements.contains(it) }.let { filtered ->
        copy(
            elements = this.elements - filtered.toSet(),
            tombstones = this.tombstones + filtered.map(::tombstone).toSet()
        )
    }

    override fun merge(other: TombstoneSet<T, K>): TombstoneSet<T, K> {
        val allTombstones = tombstones + other.tombstones
        val allElements = elements + other.elements
        return copy(
            elements = allElements.filterNot { allTombstones.contains(tombstone(it)) }.toSet(),
            tombstones = allTombstones
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun tombstone(element: T): K = tombstoneResolver?.tombstoneOf(element) ?: element as K
}

fun <T> tombstoneSetOf(vararg elements: T) = TombstoneSet(elements.toSet(), setOf<T>())
fun <T, K> tombstoneSetOf(vararg elements: T, tombstoneResolver: TombstoneResolver<T, K>) = TombstoneSet(elements.toSet(), setOf(), tombstoneResolver)
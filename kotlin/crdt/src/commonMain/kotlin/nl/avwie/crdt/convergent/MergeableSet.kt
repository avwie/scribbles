package nl.avwie.crdt.convergent

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet

@kotlinx.serialization.Serializable
data class MergeableSet<T, K>(
    private val elements: PersistentSet<T>,
    private val tombstones: PersistentSet<K>,
    private val keyResolver: KeyResolver<T, K>? = null
): Set<T> by elements, Mergeable<MergeableSet<T, K>> {

    fun add(element: T): MergeableSet<T, K> = when {
        tombstones.contains(key(element)) -> this
        else -> copy(elements = elements.add(element))
    }

    fun addAll(elements: Iterable<T>): MergeableSet<T, K> = elements.fold(this) { acc, element -> acc.add(element)  }
    fun addAll(vararg elements: T): MergeableSet<T, K> = addAll(elements.asIterable())

    fun remove(element: T): MergeableSet<T, K> = when {
        tombstones.contains(key(element)) -> this
        else -> copy(
            elements = elements.remove(element),
            tombstones = tombstones.add(key(element))
        )
    }

    fun removeAll(elements: Iterable<T>): MergeableSet<T, K> = elements.fold(this) { acc, element -> acc.remove(element) }
    fun removeAll(vararg elements: T): MergeableSet<T, K> = removeAll(elements.asIterable())

    override fun merge(other: MergeableSet<T, K>): MergeableSet<T, K> {
        val allTombstones = tombstones.addAll(other.tombstones)
        val leftElements = elements.filter { !allTombstones.contains(key(it)) }.associateBy { key(it) }
        val rightElements = other.elements.filter { !allTombstones.contains(key(it)) }.associateBy { key(it) }

        val intersectKeys = leftElements.keys.intersect(rightElements.keys)
        val differenceKeys = (leftElements.keys + rightElements.keys) - intersectKeys

        val difference = leftElements.filterKeys { differenceKeys.contains(it) } + rightElements.filterKeys { differenceKeys.contains(it) }
        val intersect =  intersectKeys.map { key ->
            val (left, right) = leftElements[key]!! to rightElements[key]!!

            @Suppress("UNCHECKED_CAST")
            when (left) {
                right -> right
                is Mergeable<*> -> (left  as Mergeable<T>).merge(right)
                else -> throw IllegalStateException("Impossible to merge two values with same keys that aren't mergable!")
            }
        }

        return copy(
            elements = (difference.values + intersect).toPersistentSet(),
            tombstones = allTombstones
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun key(element: T): K = keyResolver?.key(element) ?: element as K
}

fun <T, K> mergeableSetOf(elements: Iterable<T>, keyResolver: KeyResolver<T, K>?): MergeableSet<T, K> = MergeableSet(elements.toPersistentSet(), persistentSetOf(), keyResolver)
fun <T> mergeableSetOf(elements: Iterable<T>): MergeableSet<T, T> = mergeableSetOf(elements, null)
fun <T, K> mergeableSetOf(vararg elements: T, keyResolver: KeyResolver<T, K>?): MergeableSet<T, K> = mergeableSetOf(elements.asIterable(), keyResolver)
fun <T> mergeableSetOf(vararg elements: T): MergeableSet<T, T> = mergeableSetOf(elements.asIterable(), null)

package nl.avwie.crdt.convergent

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentMap

data class MergeableMap<K, V>(
    private val map: PersistentMap<K, V>,
    private val tombstones: PersistentSet<K>
): Map<K, V> by map, Mergeable<MergeableMap<K, V>> {

    fun add(key: K, value: V): MergeableMap<K, V> = when {
        tombstones.contains(key) -> this
        else -> copy(map = map.put(key, value))
    }

    fun remove(key: K, value: V): MergeableMap<K, V> = when {
        tombstones.contains(key) -> this
        else -> copy(
            map = map.remove(key),
            tombstones = tombstones.add(key)
        )
    }

    override fun merge(other: MergeableMap<K, V>): MergeableMap<K, V> {
        val allTombstones = tombstones.addAll(other.tombstones)
        val leftKeys = map.keys.filter { !allTombstones.contains(it) }.toSet()
        val rightKeys = other.map.keys.filter { !allTombstones.contains(it) }.toSet()

        val intersectedKeys = leftKeys.intersect(rightKeys)
        val differenceKeys = leftKeys.plus(rightKeys).minus(intersectedKeys)

        val differenceMap = differenceKeys.associateWith { key -> map[key] ?: other.map[key]!! }

        // merge the intersect keys
        val intersectedMap = intersectedKeys.associateWith { key ->
            val (left, right) = map[key]!! to other.map[key]!!

            @Suppress("UNCHECKED_CAST")
            when (left) {
                right -> right
                is Mergeable<*> -> (left as Mergeable<V>).merge(right)
                else -> throw IllegalStateException("Impossible to merge two values with same keys that aren't mergable!")
            }
        }

        return MergeableMap(
            map = (intersectedMap + differenceMap).toPersistentMap(),
            tombstones = allTombstones
        )
    }
}
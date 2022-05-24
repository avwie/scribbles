package nl.avwie.crdt.convergent

import kotlinx.collections.immutable.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@kotlinx.serialization.Serializable
data class MergeableMap<K, V>(
    private val map: PersistentMap<K, V>,
    private val timestamps: PersistentMap<K, Instant>,
    private val tombstones: PersistentSet<K>
): Map<K, V> by map, Mergeable<MergeableMap<K, V>> {

    fun put(key: K, value: V): MergeableMap<K, V> = when {
        tombstones.contains(key) -> this
        else -> copy(
            map = map.put(key, value),
            timestamps = timestamps.put(key, Clock.System.now())
        )
    }

    fun remove(key: K): MergeableMap<K, V> = when {
        tombstones.contains(key) -> this
        !map.containsKey(key) -> this
        else -> copy(
            map = map.remove(key),
            timestamps.remove(key),
            tombstones = tombstones.add(key)
        )
    }

    override fun merge(other: MergeableMap<K, V>): MergeableMap<K, V> {
        val allTombstones = tombstones.addAll(other.tombstones)
        val allKeys = (map.keys + other.map.keys) - allTombstones

        val timestampsBuilder = persistentMapOf<K, Instant>().builder()
        val elementsBuilder = persistentMapOf<K, V>().builder()

        allKeys.forEach { key ->
            val (left, right) = get(key) to other.get(key)
            val (timestampLeft, timestampRight) = timestamps[key] to other.timestamps[key]
            when {
                left != null && right == null -> {
                    elementsBuilder[key] = left
                    timestampsBuilder[key] = timestamps[key]!!
                }

                left == null && right != null -> {
                    elementsBuilder[key] = right
                    timestampsBuilder[key] = timestampRight!!
                }

                timestampLeft!! > timestampRight!! -> {
                    elementsBuilder[key] = left!!
                    timestampsBuilder[key] = timestampLeft
                }

                timestampLeft < timestampRight -> {
                    elementsBuilder[key] = right!!
                    timestampsBuilder[key] = timestampRight
                }

                left.hashCode() >= right!!.hashCode() ->  {
                    elementsBuilder[key] = left!!
                    timestampsBuilder[key] = timestampLeft
                }

                left.hashCode() < right.hashCode() ->  {
                    elementsBuilder[key] = right
                    timestampsBuilder[key] = timestampRight
                }
            }
        }

        return MergeableMap(
            map = elementsBuilder.build(),
            timestamps = timestampsBuilder.build(),
            tombstones = allTombstones
        )
    }
}

fun <K, V> mergeableMapOf(map: Map<K, V>): MergeableMap<K, V> = MergeableMap(
    map.toPersistentMap(),
    map.keys.associateWith { Clock.System.now() }.toPersistentMap(),
    persistentSetOf()
)

fun <K, V> mergeableMapOf(pairs: Iterable<Pair<K, V>>): MergeableMap<K, V> = mergeableMapOf(pairs.toMap())
fun <K, V> mergeableMapOf(vararg pairs: Pair<K, V>): MergeableMap<K, V> = mergeableMapOf(pairs.asIterable())
fun <K, V> mergeabeMapOf(): MergeableMap<K, V> = mergeableMapOf(mapOf())

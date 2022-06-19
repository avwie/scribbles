package nl.avwie.crdt.convergent

import kotlinx.collections.immutable.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer

@kotlinx.serialization.Serializable(with = MergeableMapSerializer::class)
class MergeableMap<K, V>(
    val map: PersistentMap<K, MergeableValue<V>>,
    val tombstones: PersistentSet<K>
): Map<K, V>, Mergeable<MergeableMap<K, V>> {

    fun put(key: K, value: V): MergeableMap<K, V> = when {
        tombstones.contains(key) -> this
        else -> MergeableMap(
            map = map.put(key, mergeableValueOf(value)),
            tombstones = tombstones
        )
    }

    fun remove(key: K): MergeableMap<K, V> = when {
        tombstones.contains(key) -> this
        !map.containsKey(key) -> this
        else -> MergeableMap(
            map = map.remove(key),
            tombstones = tombstones.add(key)
        )
    }

    override val entries: Set<Map.Entry<K, V>> = map.entries.map { (k, v) ->
        object : Map.Entry<K, V> {
            override val key: K = k
            override val value: V = v.value
        }
    }.toSet()

    override val keys: Set<K> = map.keys
    override val size: Int = map.size
    override val values: Collection<V> = map.values.map { it.value }
    override fun containsKey(key: K): Boolean = map.containsKey(key)
    override fun containsValue(value: V): Boolean = map.containsValue(mergeableValueOf(value))
    override fun get(key: K): V? = map[key]?.value
    override fun isEmpty(): Boolean = map.isEmpty()

    override fun merge(other: MergeableMap<K, V>): MergeableMap<K, V> {
        val allTombstones = tombstones.addAll(other.tombstones)
        val allKeys = (map.keys + other.map.keys) - allTombstones
        val elementsBuilder = persistentMapOf<K, MergeableValue<V>>().builder()

        allKeys.forEach { key ->
            val (left, right) = map[key] to other.map[key]
            val winner = when {
                left != null && right == null -> left
                left == null && right != null -> right
                else -> left!!.merge(right!!)
            }
            elementsBuilder[key] = winner
        }

        return MergeableMap(
            map = elementsBuilder.build(),
            tombstones = allTombstones
        )
    }
}

class MergeableMapSerializer<K, V>(keySerializer: KSerializer<K>, valueSerializer: KSerializer<V>) : KSerializer<MergeableMap<K, V>> {

    private val mapSerializer = MapSerializer(keySerializer, MergeableValue.serializer(valueSerializer))
    private val tombstonesSerializer = SetSerializer(keySerializer)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("mergeableMap") {
        element("map", descriptor = mapSerializer.descriptor)
        element("tombstones", descriptor = tombstonesSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MergeableMap<K, V>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, mapSerializer, value.map)
            encodeSerializableElement(descriptor, 1, tombstonesSerializer, value.tombstones)
        }
    }

    override fun deserialize(decoder: Decoder): MergeableMap<K, V> = decoder.decodeStructure(descriptor) {
        var map: Map<K, MergeableValue<V>> = mapOf()
        var tombstones: Set<K> = setOf()

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> map = decodeSerializableElement(descriptor, index, mapSerializer)
                1 -> tombstones = decodeSerializableElement(descriptor, index, tombstonesSerializer)
                CompositeDecoder.DECODE_DONE -> break
            }
        }
        MergeableMap(map.toPersistentMap(), tombstones.toPersistentSet())
    }
}

fun <K, V> mergeableMapOf(map: Map<K, V>): MergeableMap<K, V> = MergeableMap(
    map.mapValues { (_, v) -> mergeableValueOf(v) }.toPersistentMap(),
    persistentSetOf()
)

fun <K, V> mergeableMapOf(pairs: Iterable<Pair<K, V>>): MergeableMap<K, V> = mergeableMapOf(pairs.toMap())
fun <K, V> mergeableMapOf(vararg pairs: Pair<K, V>): MergeableMap<K, V> = mergeableMapOf(pairs.asIterable())
fun <K, V> mergeabeMapOf(): MergeableMap<K, V> = mergeableMapOf(mapOf())

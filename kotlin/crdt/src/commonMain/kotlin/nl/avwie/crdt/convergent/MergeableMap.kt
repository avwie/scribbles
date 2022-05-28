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
data class MergeableMap<K, V>(
    val map: PersistentMap<K, V>,
    val timestamps: PersistentMap<K, Instant>,
    val tombstones: PersistentSet<K>
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
            val (element, elementTimestamp) = determine(left, right, timestampLeft, timestampRight)
            elementsBuilder[key] = element
            timestampsBuilder[key] = elementTimestamp
        }

        return MergeableMap(
            map = elementsBuilder.build(),
            timestamps = timestampsBuilder.build(),
            tombstones = allTombstones
        )
    }

    private fun determine(
        left: V?,
        right: V?,
        leftTimestamp: Instant?,
        rightTimestamp: Instant?
    ): Pair<V, Instant> = when {
        left != null && right == null -> left to leftTimestamp!!
        left == null && right != null -> right to rightTimestamp!!

        left == null || right == null -> throw IllegalStateException("Items can't be null now")
        leftTimestamp == null || rightTimestamp == null -> throw IllegalStateException("Timestamps can't be null")

        leftTimestamp >= rightTimestamp -> left to leftTimestamp
        leftTimestamp < rightTimestamp -> right to rightTimestamp
        left.hashCode() >= right.hashCode() -> left to leftTimestamp
        else -> right to rightTimestamp
    }
}

class MergeableMapSerializer<K, V>(keySerializer: KSerializer<K>, valueSerializer: KSerializer<V>) : KSerializer<MergeableMap<K, V>> {

    private val mapSerializer = MapSerializer(keySerializer, valueSerializer)
    private val timestampsSerializer = MapSerializer(keySerializer, Instant.serializer())
    private val tombstonesSerializer = SetSerializer(keySerializer)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("mergeableMap") {
        element("map", descriptor = mapSerializer.descriptor)
        element("timestamps", descriptor = timestampsSerializer.descriptor)
        element("tombstones", descriptor = tombstonesSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MergeableMap<K, V>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, mapSerializer, value.map)
            encodeSerializableElement(descriptor, 1, timestampsSerializer, value.timestamps)
            encodeSerializableElement(descriptor, 2, tombstonesSerializer, value.tombstones)
        }
    }

    override fun deserialize(decoder: Decoder): MergeableMap<K, V> = decoder.decodeStructure(descriptor) {
        var map: Map<K, V> = mapOf()
        var timestamps: Map<K, Instant> = mapOf()
        var tombstones: Set<K> = setOf()

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> map = decodeSerializableElement(descriptor, index, mapSerializer)
                1 -> timestamps = decodeSerializableElement(descriptor, index, timestampsSerializer)
                2 -> tombstones = decodeSerializableElement(descriptor, index, tombstonesSerializer)
                CompositeDecoder.DECODE_DONE -> break
            }
        }
        MergeableMap(map.toPersistentMap(), timestamps.toPersistentMap(), tombstones.toPersistentSet())
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

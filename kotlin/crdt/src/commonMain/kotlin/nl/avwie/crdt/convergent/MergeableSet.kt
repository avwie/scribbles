package nl.avwie.crdt.convergent

@kotlinx.serialization.Serializable
data class MergeableSet<T, K>(
    private val underlyingMap: MergeableMap<K, T>,
    private val keyResolver: KeyResolver<T, K>? = null
): Set<T>, Mergeable<MergeableSet<T, K>> {

    fun add(element: T): MergeableSet<T, K> = copy(
        underlyingMap = underlyingMap.put(key(element), element)
    )

    fun addAll(elements: Iterable<T>) = elements.fold(this) { acc, el -> acc.add(el) }
    fun addAll(vararg elements: T) = addAll(elements.asIterable())

    fun remove(element: T): MergeableSet<T, K> = copy(
        underlyingMap = underlyingMap.remove(key(element))
    )

    fun removeAll(elements: Iterable<T>) = elements.fold(this) { acc, el -> acc.remove(el) }
    fun removeAll(vararg elements: T) = removeAll(elements.asIterable())

    override val size: Int  get() = underlyingMap.size
    override fun contains(element: T): Boolean = underlyingMap.containsValue(element)
    override fun containsAll(elements: Collection<T>): Boolean = elements.all(::contains)
    override fun isEmpty(): Boolean = underlyingMap.isEmpty()
    override fun iterator(): Iterator<T> = underlyingMap.values.iterator()

    override fun merge(other: MergeableSet<T, K>): MergeableSet<T, K> = copy(
        underlyingMap = underlyingMap.merge(other.underlyingMap)
    )

    @Suppress("UNCHECKED_CAST")
    private fun key(element: T): K = keyResolver?.key(element) ?: element as K
}

@Suppress("UNCHECKED_CAST")
fun <T, K> mergeableSetOf(elements: Iterable<T>, keyResolver: KeyResolver<T, K>?): MergeableSet<T, K> = elements.map { value ->
    val key = keyResolver?.key(value) ?: value as K
    key to value
}.toMap().let { map ->  MergeableSet(mergeableMapOf(map), keyResolver) }

fun <T> mergeableSetOf(elements: Iterable<T>): MergeableSet<T, T> = mergeableSetOf(elements, null)
fun <T, K> mergeableSetOf(vararg elements: T, keyResolver: KeyResolver<T, K>?): MergeableSet<T, K> = mergeableSetOf(elements.asIterable(), keyResolver)
fun <T> mergeableSetOf(vararg elements: T): MergeableSet<T, T> = mergeableSetOf(elements.asIterable(), null)

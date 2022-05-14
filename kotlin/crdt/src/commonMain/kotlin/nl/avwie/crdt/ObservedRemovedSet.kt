package nl.avwie.crdt

@kotlinx.serialization.Serializable
class ObservedRemovedSet<T, K>(
    private val elements: MutableSet<T> = mutableSetOf(),
    private val tombstones: GrowOnlySet<K> = growOnlySetOf(),
    private val tombstoneResolver: TombstoneResolver<T, K>? = null
) : MutableSet<T>, Mergeable<ObservedRemovedSet<T, K>>{

    class MutableIterator<T, K>(
        private val iterator: Iterator<T>,
        private val tombstones: GrowOnlySet<K>,
        private val getTombstone: (T) -> K
    ) : kotlin.collections.MutableIterator<T>, Iterator<T> by iterator {
        private var  current: T? = null

        override fun next(): T = iterator.next().also {
            current = it
        }

        override fun remove() {
            current?.also { tombstones.add(getTombstone(it)) }
        }
    }

    override fun add(element: T): Boolean = when {
        tombstones.contains(getTombstone(element)) -> false
        else -> elements.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean = elements.map(::add).any()

    override fun clear() {
        tombstones.addAll(elements.map { getTombstone(it) })
    }

    override fun iterator(): kotlin.collections.MutableIterator<T> = MutableIterator(elements.iterator(), tombstones) { getTombstone(it) }

    override fun remove(element: T): Boolean = when {
        !contains(element) -> false
        else -> {
            elements.remove(element)
            tombstones.add(getTombstone(element))
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean = elements.map(::remove).any()
    override fun retainAll(elements: Collection<T>): Boolean = removeAll(elements.filterNot(::contains))

    override val size: Int
        get() = elements.size

    override fun contains(element: T): Boolean = elements.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = elements.none { !contains(it) }

    override fun isEmpty(): Boolean = size == 0

    override fun merge(other: ObservedRemovedSet<T, K>): ObservedRemovedSet<T, K> {
        val tombstones = tombstones.merge(other.tombstones)
        val elements = (elements + other.elements).filterNot { element -> tombstones.contains(getTombstone(element)) }
        return ObservedRemovedSet(
            elements = elements.toMutableSet(),
            tombstones = tombstones,
            tombstoneResolver = tombstoneResolver
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun getTombstone(item: T): K = tombstoneResolver?.tombstoneOf(item) ?: item as K
}

fun <T, K> observedRemovedSetOf(vararg elements: T, tombstoneStrategy: TombstoneResolver<T, K>?) = ObservedRemovedSet(elements.toMutableSet(), growOnlySetOf(), tombstoneStrategy)
fun <T> observedRemovedSetOf(vararg elements: T) = observedRemovedSetOf<T, T>(*elements, tombstoneStrategy = null)
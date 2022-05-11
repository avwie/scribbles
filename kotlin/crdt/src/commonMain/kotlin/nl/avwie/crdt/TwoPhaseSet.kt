package nl.avwie.crdt

class TwoPhaseSet<T, K>(
    private val growOnlySet: GrowOnlySet<T> = growOnlySetOf(),
    private val tombstones: GrowOnlySet<K> = growOnlySetOf(),
    private val getTombstone: (T) -> K
) : MutableSet<T>, Mergeable<TwoPhaseSet<T, K>>{

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
        else -> growOnlySet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean = elements.map(::add).any()

    override fun clear() {
        tombstones.addAll(growOnlySet.map(getTombstone))
    }

    override fun iterator(): kotlin.collections.MutableIterator<T> = MutableIterator(growOnlySet.iterator(), tombstones,getTombstone)

    override fun remove(element: T): Boolean = when {
        !contains(element) -> false
        else -> tombstones.add(getTombstone(element))
    }

    override fun removeAll(elements: Collection<T>): Boolean = elements.map(::remove).any()
    override fun retainAll(elements: Collection<T>): Boolean = removeAll(elements.filterNot(::contains))

    override val size: Int
        get() = growOnlySet.size - tombstones.size

    override fun contains(element: T): Boolean = when {
        tombstones.contains(getTombstone(element)) -> false
        growOnlySet.contains(element) -> true
        else -> false
    }

    override fun containsAll(elements: Collection<T>): Boolean = elements.none { !contains(it) }

    override fun isEmpty(): Boolean = size == 0

    override fun merge(other: TwoPhaseSet<T, K>): TwoPhaseSet<T, K> {
        return TwoPhaseSet(
            growOnlySet = growOnlySet.merge(other.growOnlySet),
            tombstones = tombstones.merge(other.tombstones),
            getTombstone = getTombstone
        )
    }
}

fun <T, K> twoPhaseSetOf(vararg elements: T, getTombStoneKey: (T) -> K) = TwoPhaseSet(growOnlySetOf(*elements), growOnlySetOf(), getTombStoneKey)
fun <T> twoPhaseSetOf(vararg elements: T) = twoPhaseSetOf(*elements) { it }
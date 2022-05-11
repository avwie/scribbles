package nl.avwie.crdt

class GrowOnlySet<T>(
    private val backingSet: MutableSet<T> = mutableSetOf<T>()
) : Mergeable<GrowOnlySet<T>>, Set<T> by backingSet  {

    fun add(element: T): Boolean = backingSet.add(element)
    fun addAll(elements: Collection<T>): Boolean = elements.map(::add).any()

    override fun merge(other: GrowOnlySet<T>): GrowOnlySet<T> {
        val elements = mutableSetOf<T>()
        elements.addAll(this)
        elements.addAll(other)
        return GrowOnlySet(elements)
    }
}

fun <T> growOnlySetOf(vararg elements: T): GrowOnlySet<T> = GrowOnlySet(backingSet = mutableSetOf(*elements))
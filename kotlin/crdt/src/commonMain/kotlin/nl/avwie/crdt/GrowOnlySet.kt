package nl.avwie.crdt

@kotlinx.serialization.Serializable
class GrowOnlySet<T>(
    private val elements: MutableSet<T> = mutableSetOf()
) : Mergeable<GrowOnlySet<T>>, Set<T> by elements  {

    fun add(element: T): Boolean = elements.add(element)
    fun addAll(elements: Collection<T>): Boolean = elements.map(::add).any()

    override fun merge(other: GrowOnlySet<T>): GrowOnlySet<T> {
        val elements = mutableSetOf<T>()
        elements.addAll(this)
        elements.addAll(other)
        return GrowOnlySet(elements)
    }
}

fun <T> growOnlySetOf(vararg elements: T): GrowOnlySet<T> = GrowOnlySet(elements = mutableSetOf(*elements))
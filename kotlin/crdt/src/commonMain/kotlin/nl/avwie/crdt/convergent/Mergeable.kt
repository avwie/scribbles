package nl.avwie.crdt.convergent

interface Mergeable<T> {
    fun merge(other: T): T
}

fun <T : Mergeable<T>> merge(mergables: Iterable<T>): T = mergables.reduce { left, right -> left.merge(right) }
fun <T : Mergeable<T>> merge(vararg mergeables: T): T = merge(mergeables.asIterable())
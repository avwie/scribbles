package nl.avwie.crdt

fun <T : Mergeable<T>> merge(vararg mergeables: T): T = mergeables.reduce { a, b -> a.merge(b) }
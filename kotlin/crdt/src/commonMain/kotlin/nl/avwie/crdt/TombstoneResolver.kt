package nl.avwie.crdt

interface TombstoneResolver<T, K> {
    fun tombstoneOf(item: T): K
}
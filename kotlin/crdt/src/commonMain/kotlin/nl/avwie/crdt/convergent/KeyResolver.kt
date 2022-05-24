package nl.avwie.crdt.convergent

interface KeyResolver<T, K> {
    fun key(item: T): K
}
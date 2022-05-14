package nl.avwie.crdt

import kotlinx.serialization.SerialName

interface TombstoneResolver<T, K> {
    fun tombstoneOf(item: T): K
}
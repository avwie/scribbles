package nl.avwie.crdt

interface Mergeable<T> {
    fun merge(other: T): T
}

fun <T> T.asMergeable(): Mergeable<T> = object : Mergeable<T> {
    override fun merge(other: T): T {
        return other
    }
}

fun <T> T.merge(other: T) = this.asMergeable().merge(other)
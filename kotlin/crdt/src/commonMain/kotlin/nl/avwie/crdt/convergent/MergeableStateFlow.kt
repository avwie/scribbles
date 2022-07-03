package nl.avwie.crdt.convergent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

interface MergeableStateFlow<T : Mergeable<T>> : Mergeable<T>, StateFlow<T> {
    fun update(block: (state: T) -> T)
}

private class MergeableStateFlowImpl<T : Mergeable<T>>(
    private val _states: MutableStateFlow<T>
) : MergeableStateFlow<T>, StateFlow<T> by _states {

    override fun merge(other: T): T {
        _states.update { current -> current.merge(other) }
        return value
    }

    override fun update(block: (state: T) -> T) {
        _states.update(block)
    }
}

fun <T : Mergeable<T>> T.asStateFlow(): MergeableStateFlow<T> = MergeableStateFlowImpl(MutableStateFlow(this))
fun <T : Mergeable<T>> MergeableStateFlow<T>.mergeWith(other: MergeableStateFlow<T>, scope: CoroutineScope) {
    other.onEach { update -> this.merge(update) }.launchIn(scope)
}
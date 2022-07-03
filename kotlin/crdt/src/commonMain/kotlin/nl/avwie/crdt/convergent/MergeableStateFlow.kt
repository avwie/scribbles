package nl.avwie.crdt.convergent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nl.avwie.common.messagebus.MessageBus

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

fun <T: Mergeable<T>> MergeableStateFlow<T>.broadcast(
    messageBus: MessageBus<T>,
    scope: CoroutineScope
) {
    this.onEach { update ->
        messageBus.send(update)
    }.launchIn(scope)

    messageBus.messages.onEach { update ->
        val merged = this.merge(update)
        if (merged != update) {
            messageBus.send(update)
        }
    }.launchIn(scope)

    // initial publish
    scope.launch {
        messageBus.send(value)
    }
}
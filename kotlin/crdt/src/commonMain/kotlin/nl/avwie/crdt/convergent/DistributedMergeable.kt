package nl.avwie.crdt.convergent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import nl.avwie.common.UUID
import nl.avwie.common.uuid
import kotlin.coroutines.EmptyCoroutineContext

data class DistributedMergeable<T : Mergeable<T>>(
    val states: MutableStateFlow<T>,
    val updates: MutableSharedFlow<Update<T>>
) {
    data class Update<T : Mergeable<T>>(val source: UUID, val value: T)
}

fun <T : Mergeable<T>> T.distribute(
    updates: MutableSharedFlow<DistributedMergeable.Update<T>>,
    scope: CoroutineScope
): DistributedMergeable<T> {
    val source = uuid()
    val states = MutableStateFlow(this)

    states.onEach { newState ->
        updates.emit(DistributedMergeable.Update(source, newState))
    }.launchIn(scope)

    updates.onEach { update ->
        if (update.source == source) {
            println("Same source: $update")
            return@onEach
        }
        if (update.value == states.value) {
            println("Same state: $update")
            return@onEach
        }
        val merged = states.value.merge(update.value)
        println("Merged: $update -> $merged")
        states.value = merged
    }.launchIn(scope)

    return DistributedMergeable(states, updates)
}

fun <T : Mergeable<T>> T.distribute(scope: CoroutineScope): DistributedMergeable<T> = distribute(MutableSharedFlow(), scope)
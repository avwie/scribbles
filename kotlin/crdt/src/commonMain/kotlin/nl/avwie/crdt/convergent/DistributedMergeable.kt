package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import nl.avwie.common.UUID
import nl.avwie.common.uuid

data class Update<T>(val sourceId: UUID, val data: T)

fun <T : Mergeable<T>> T.distributeIn(
    scope: CoroutineScope,
    updates: MutableSharedFlow<Update<T>>
): MutableStateFlow<T> {
    val sourceId = uuid()
    val states: MutableStateFlow<T> = MutableStateFlow(this)

    scope.launch {
        states.onEach { newState ->
            updates.emit(Update(sourceId, newState))
        }.launchIn(this)

        updates.onEach { update ->
            if (update.sourceId == sourceId) return@onEach
            if (update.data == states.value) return@onEach

            val merged = states.value.merge(update.data)
            states.value = merged
        }.launchIn(this)
    }
    return states
}

fun <T : Mergeable<T>> T.distribute(
    scope: CoroutineScope
): Pair<MutableStateFlow<T>, MutableSharedFlow<Update<T>>> {
    val updates = MutableSharedFlow<Update<T>>()
    val states = distributeIn(scope, updates)
    return states to updates
}
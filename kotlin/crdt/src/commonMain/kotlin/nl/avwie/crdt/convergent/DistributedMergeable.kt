package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import nl.avwie.common.coroutines.DistributedMessage
import nl.avwie.common.uuid

fun <T : Mergeable<T>> T.distributeIn(
    scope: CoroutineScope,
    updates: MutableSharedFlow<DistributedMessage<T>>
): MutableStateFlow<T> {
    val clientId = uuid()
    val states: MutableStateFlow<T> = MutableStateFlow(this)

    scope.launch {
        states.onEach { newState ->
            updates.emit(DistributedMessage(clientId, newState))
        }.launchIn(this)

        updates.onEach { update ->
            if (update.clientId == clientId) return@onEach
            if (update.contents == states.value) return@onEach

            val merged = states.value.merge(update.contents)
            states.value = merged
        }.launchIn(this)
    }
    return states
}

fun <T : Mergeable<T>> T.distribute(
    scope: CoroutineScope
): Pair<MutableStateFlow<T>, MutableSharedFlow<DistributedMessage<T>>> {
    val updates = MutableSharedFlow<DistributedMessage<T>>()
    val states = distributeIn(scope, updates)
    return states to updates
}
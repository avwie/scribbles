package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import nl.avwie.common.UUID
import nl.avwie.common.uuid
import kotlin.coroutines.EmptyCoroutineContext

data class DistributedMergeable<T : Mergeable<T>>(
    val states: MutableStateFlow<T>,
    val updates: MutableSharedFlow<Update<T>>,
    val job: Job
) {
    data class Update<T : Mergeable<T>>(val source: UUID, val value: T)
}

suspend fun <T : Mergeable<T>> T.distribute(
    updates: MutableSharedFlow<DistributedMergeable.Update<T>>,
    scope: CoroutineScope
): DistributedMergeable<T> {
    val source = uuid()
    val states = MutableStateFlow(this)

    val job = scope.launch {
        states.onEach { newState ->
            updates.emit(DistributedMergeable.Update(source, newState))
        }.launchIn(this)

        updates.onEach { update ->
            println(update)
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
        }.launchIn(this)
    }

    // await start
    scope.launch {
        while (job.children.any { !it.isActive }) {
            delay(1)
        }
    }.join()

    return DistributedMergeable(states, updates, job)
}

suspend fun <T : Mergeable<T>> T.distribute(scope: CoroutineScope): DistributedMergeable<T> =
    distribute(MutableSharedFlow(), scope)
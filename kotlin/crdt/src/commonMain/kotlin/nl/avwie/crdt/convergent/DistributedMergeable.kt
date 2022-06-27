package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import nl.avwie.common.UUID
import nl.avwie.common.uuid
import kotlin.coroutines.EmptyCoroutineContext

interface DistributedMergeable<T : Mergeable<T>> : Mergeable<T> {
    val source: UUID

    val states: StateFlow<T>
    fun update(block: (current: T) -> T)
    fun close()

    @kotlinx.serialization.Serializable
    data class Update<T : Mergeable<T>>(val source: UUID, val update: T)
}

val <T : Mergeable<T>> DistributedMergeable<T>.value get() = this.states.value

fun <T : Mergeable<T>> DistributedMergeable(
    initialState: T,
    source: UUID = uuid(),
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
    updates: MutableSharedFlow<DistributedMergeable.Update<T>> = MutableSharedFlow()
): DistributedMergeable<T> = DistributedMergeableImpl(initialState, source, scope, updates)

class DistributedMergeableImpl<T : Mergeable<T>>(
    initialState: T,
    override val source: UUID,
    private val scope: CoroutineScope,
    private val updates: MutableSharedFlow<DistributedMergeable.Update<T>>
) : DistributedMergeable<T>, Mergeable<T> {
    private val _states: MutableStateFlow<T> = MutableStateFlow(initialState)
    override val states: StateFlow<T> = _states
    init {
        scope.launch {
            states.onEach { newState ->
                updates.emit(DistributedMergeable.Update(source, newState))
            }.launchIn(this)

            updates.onEach { update ->
                if (update.source == source) return@onEach
                if (update.update == states.value) return@onEach

                val merged = states.value.merge(update.update)
                _states.value = merged
            }.launchIn(this)
        }
    }

    override fun update(block: (current: T) -> T) = _states.update(block)

    override fun close() {
        scope.cancel()
    }

    override fun merge(other: T): T {
        update { current -> current.merge(other) }
        return value
    }
}
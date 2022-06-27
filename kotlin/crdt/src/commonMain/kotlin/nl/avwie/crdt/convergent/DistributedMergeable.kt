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

    suspend fun awaitInitialization(): DistributedMergeable<T>
    data class Update<T : Mergeable<T>>(val source: UUID, val value: T)
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

    private val initializeJob = Job()
    init {
        states.onEach { newState ->
            updates.emit(DistributedMergeable.Update(source, newState))
        }.launchIn(scope + initializeJob)

        updates.onEach { update ->
            if (update.source == source) return@onEach
            if (update.value == states.value) return@onEach

            val merged = states.value.merge(update.value)
            _states.value = merged
        }.launchIn(scope + initializeJob)
    }

    override suspend fun awaitInitialization(): DistributedMergeable<T> {
        coroutineScope {
            launch {
                while (initializeJob.children.any { !it.isActive }) {
                    delay(1)
                }
            }.join()
        }
        return this
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
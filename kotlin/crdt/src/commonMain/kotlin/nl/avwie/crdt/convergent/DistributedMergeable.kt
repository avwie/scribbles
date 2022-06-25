package nl.avwie.crdt.convergent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import nl.avwie.common.UUID
import nl.avwie.common.uuid
import kotlin.coroutines.EmptyCoroutineContext

interface DistributedMergeable<T : Mergeable<T>> {
    val bus: MutableSharedFlow<Update<T>>
    val states: StateFlow<T>
    fun publish(update: T)

    @kotlinx.serialization.Serializable
    data class Update<T : Mergeable<T>>(val source: UUID, val value: T)
}

class DistributedMergeableImpl<T : Mergeable<T>>(
    initialValue: T,
    override val bus: MutableSharedFlow<DistributedMergeable.Update<T>>,
    val scope: CoroutineScope,
    val source: UUID,
) : DistributedMergeable<T> {

    private val _states = MutableStateFlow(initialValue)
    override val states: StateFlow<T> = _states

    init {
        // publish
        _states.onEach { newState ->
            bus.emit(DistributedMergeable.Update(source, newState))
        }.launchIn(scope)

        // subscribe
        bus.onEach { update ->
            if (update.source == source) return@onEach
            if (update.value == states.value) return@onEach
            _states.value = states.value.merge(update.value)
        }.launchIn(scope)
    }

    override fun publish(update: T) {
        _states.update { update }
    }
}

val <T : Mergeable<T>> DistributedMergeable<T>.value get() = states.value
fun <T : Mergeable<T>> DistributedMergeable<T>.update(block: (currentState: T) -> T) = publish(block(value))

fun <T : Mergeable<T>> T.asDistributed(): DistributedMergeable<T> =
    DistributedMergeableImpl(this, MutableSharedFlow(), CoroutineScope(EmptyCoroutineContext), uuid())

//fun <T : Mergeable<T>> T.distribute(): Pair<MutableStateFlow<T>, Mut
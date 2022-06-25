package nl.avwie.crdt.convergent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import nl.avwie.common.UUID
import nl.avwie.common.uuid
import kotlin.coroutines.EmptyCoroutineContext

class DistributedMergeable<T : Mergeable<T>>(
    val stateFlow: MutableStateFlow<T>,
    val sharedFlow: MutableSharedFlow<Update<T>>,
    private val sourceId: UUID,
    val scope: CoroutineScope,
): MutableStateFlow<T> by stateFlow {

    @kotlinx.serialization.Serializable
    data class Update<T : Mergeable<T>>(val sourceId: UUID, val state: T)

    init {
        // publish
        stateFlow.onEach { update ->
            println("${sourceId}: Emitting update: ${Update(sourceId, update)}")
            sharedFlow.emit(Update(sourceId, update))
        }.launchIn(scope)

        // subscribe
        sharedFlow.onEach { update ->
            println("${sourceId}: Receiving update: $update")
            if (update.sourceId == sourceId) {
                println("Received from myself!")
                return@onEach
            }

            if (update.state == stateFlow.value) {
                println("${sourceId}: Received same value: ${update.state}, ${stateFlow.value}")
                return@onEach
            }

            // different, so update
            while (true) {
                val current = stateFlow.value
                val merged = current.merge(update.state)
                if (stateFlow.compareAndSet(current, merged)) {
                    println("${sourceId}: Updated value to $merged")
                    return@onEach
                }
            }
        }.launchIn(scope)
    }

    operator fun component1() = stateFlow
    operator fun component2() = sharedFlow

    companion object {
        operator fun <T : Mergeable<T>> invoke(
            mergeable: T,
            sharedFlow: MutableSharedFlow<Update<T>> = MutableSharedFlow(),
            sourceId: UUID = uuid(),
            scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
        ) = DistributedMergeable(MutableStateFlow(mergeable), sharedFlow, sourceId, scope)
    }
}

fun <T : Mergeable<T>> T.asDistributed() = DistributedMergeable(this)
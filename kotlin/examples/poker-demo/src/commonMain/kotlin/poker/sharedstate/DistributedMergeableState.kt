package poker.sharedstate

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import nl.avwie.common.UUID
import nl.avwie.common.messagebus.MessageBus
import nl.avwie.common.messagebus.SerializingMessageBus
import nl.avwie.common.uuid
import nl.avwie.crdt.convergent.Mergeable
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty

class DistributedMergeableState<T : Mergeable<T>>(
    private val mutableState: MutableState<T>,
    private val messageBus: MessageBus<Update<T>>,
    private val originator: UUID = uuid(),
    private val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) : MutableState<T> by mutableState {

    @kotlinx.serialization.Serializable
    data class Update<T>(val originator: UUID, val state: T)

    private var lastUpdate: Update<T>? = null

    init {
        println("Initial state:")
        println(value)

        messageBus.messages
            .filter { update -> update.originator != originator }
            .onEach { update ->
                println("Receiving from ${update.originator}")
                if (update.state != value) {
                    println("Updated state is different from own state")
                    val merged = value.merge(update.state)
                    if (value != merged) {
                        println("Merged state is different from own state")
                        value = merged
                    } else {
                        println("Merged state is same from own state, but need to publish anyway")
                        publish(force = true)
                    }
                }
            }
            .launchIn(scope)

        snapshotFlow { value }
            .onEach {
                println("My own state has changed, so I need to publish")
                println(value)
                publish()
            }
            .launchIn(scope)
    }

    fun publish(force: Boolean = false) {
        val update = Update(originator, this.value)
        println("Trying to publish (forced = $force)")
        if (force || update != lastUpdate) {
            println("Current update is different than last one or we are force (forced = $force)")
            lastUpdate = update
            scope.launch {
                messageBus.send(Update(originator, this@DistributedMergeableState.value))
            }
        } else {
            println("Tried publishing, but message is the same as previous one")
        }
    }
}

inline fun <reified T : Mergeable<T>> distributedMergeableStateOf(
    initialState: T,
    messageBus: MessageBus<String>,
    serializerModule: SerializersModule = EmptySerializersModule,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) : DistributedMergeableState<T> {
    val serializingMessageBus = SerializingMessageBus<DistributedMergeableState.Update<T>>(messageBus, serializerModule = serializerModule)
    return DistributedMergeableState(mutableStateOf(initialState), serializingMessageBus, uuid(), scope)
}
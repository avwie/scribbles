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
        messageBus.messages
            .filter { update -> update.originator != originator }
            .onEach { update ->
                if (update.state != value) {
                    val merged = value.merge(update.state)
                    if (value != merged) {
                        println("Newly merged was different than current")
                        value = merged
                    } else {
                        println("Merged was same as current, but update was different, so publishing")
                        publish()
                    }
                }
            }
            .launchIn(scope)

        snapshotFlow { value }
            .onEach {
                println("Sending value!")
                publish()
            }
            .launchIn(scope)
    }

    fun publish() {
        val update = Update(originator, this.value)
        if (update != lastUpdate) {
            lastUpdate = update
            scope.launch {
                println("Publishing!")
                messageBus.send(Update(originator, this@DistributedMergeableState.value))
            }
        } else {
            println("Updates are the same, so not updating...")
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
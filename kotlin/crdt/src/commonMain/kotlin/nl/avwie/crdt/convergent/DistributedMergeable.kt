package nl.avwie.crdt.convergent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import nl.avwie.common.UUID
import nl.avwie.common.messagebus.MessageBus
import nl.avwie.common.messagebus.SerializingMessageBus
import nl.avwie.common.uuid
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty

class DistributedMergeable<T>(
    initialState: T,
    private val messageBus: MessageBus<StateUpdate<T>>,
    private val originator: UUID = uuid(),
    private val scope: CoroutineScope
) where T : Mergeable<T> {

    @kotlinx.serialization.Serializable
    data class StateUpdate<T>(val originator: UUID, val state: T)

    private val _states: MutableStateFlow<T> = MutableStateFlow(initialState)
    val states : StateFlow<T> get() = _states
    val current: T get() = _states.value

    init {
        messageBus.messages
            .filter { update -> update.originator != originator }
            .onEach { update ->
                val merged = current.merge(update.state)
                if (update.state != current) {
                    set(merged)
                }
            }.launchIn(scope)
    }

    fun set(state: T) {
        _states.value = state
        publish()
    }

    fun update(updater: T.() -> T) {
        set(updater(current))
    }

    fun publish() {
        scope.launch {
            messageBus.send(StateUpdate(originator, _states.value))
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = current
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { set(value) }
}

inline fun <reified T : Mergeable<T>> distributedMergeableOf(
    initialState: T,
    messageBus: MessageBus<String>,
    serializerModule: SerializersModule = EmptySerializersModule,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) : DistributedMergeable<T> {
    val serializingMessageBus = SerializingMessageBus<DistributedMergeable.StateUpdate<T>>(messageBus, serializerModule = serializerModule)
    return DistributedMergeable(initialState, serializingMessageBus, uuid(), scope)
}
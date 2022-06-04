package poker.viewmodel

import androidx.compose.runtime.*
import common.messagebus.MessageBus
import common.persistence.KeyValueStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import poker.model.Participant
import poker.model.RoomModel
import kotlin.coroutines.CoroutineContext

enum class ViewState {
    CreateOrJoin,
    ParticipantInfo,
    Room
}

class ViewModel(
    private val messageBus: MessageBus<RoomModel>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    init {
        messageBus.messages
            .onEach { incoming -> merge(incoming) }
            .launchIn(scope)
    }

    var viewState by mutableStateOf(ViewState.CreateOrJoin)
        private set

    private var _roomState = mutableStateOf(RoomModel(""))
    var roomState : RoomModel get() = _roomState.value
        private set(value) {
            _roomState.value = value
            publish()
        }

    var participant by mutableStateOf<Participant?>(null)
        private set

    val participantCount by derivedStateOf { roomState.participants.count() }

    fun enterRoomName(name: String) {
        roomState = roomState.setName(name)
        viewState = ViewState.ParticipantInfo
    }

    fun enterParticipantName(name: String) {
        participant = Participant(name)
        roomState = roomState.putParticipant(participant!!)
        viewState = ViewState.Room
    }

    fun leave() {
        participant?.also {
            roomState = roomState.removeParticipant(it.uuid)
        }
    }

    private fun merge(other: RoomModel) {
        if (roomState != other) {
            roomState = roomState.merge(other)
        }
    }

    private fun publish() {
        scope.launch {
            println("Publishing")
            messageBus.send(roomState)
        }
    }
}
package poker.viewmodel

import androidx.compose.runtime.*
import common.messagebus.MessageBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nl.avwie.crdt.convergent.distributedMergeableOf
import poker.model.Participant
import poker.model.RoomModel
import kotlin.coroutines.EmptyCoroutineContext

enum class ViewState {
    CreateOrJoin,
    ParticipantInfo,
    Room
}

fun <T> StateFlow<T>.collectAsState(scope: CoroutineScope): State<T> {
    val state = mutableStateOf(this.value)
    this.onEach { update ->
        state.value = update
    }.launchIn(scope)
    return state
}

class ViewModel(
    messageBus: MessageBus<String>,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) {

    var viewState by mutableStateOf(ViewState.CreateOrJoin)

    private var distributedState = distributedMergeableOf(RoomModel(""), messageBus, scope = scope)
    val roomState: RoomModel by distributedState.states.collectAsState(scope)

    val participantCount by derivedStateOf { roomState.participants.count() }

    var participant by mutableStateOf<Participant?>(null)
        private set

    fun enterRoomName(name: String) {
        distributedState.update { setName(name) }
        viewState = ViewState.ParticipantInfo
    }

    fun enterParticipantName(name: String) {
        participant = Participant(name)
        distributedState.update { putParticipant(participant!!) }
        viewState = ViewState.Room
    }

    fun leave() {
        participant?.also { participant ->
            distributedState.update { removeParticipant(participant.uuid) }
        }
    }
}
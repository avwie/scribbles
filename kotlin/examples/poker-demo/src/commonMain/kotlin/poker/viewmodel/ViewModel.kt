package poker.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import nl.avwie.common.UUID
import nl.avwie.crdt.convergent.DistributedMergeable
import poker.sharedstate.Participant
import poker.sharedstate.RoomSharedState
import poker.util.collectAsState
import kotlin.coroutines.EmptyCoroutineContext

sealed class ViewModel {
    open fun leave() {}
}

class ErrorViewModel(val title: String, val message: String) : ViewModel()

class CreateViewModel(
    private val onCreateRoom: (roomName: String) -> Unit = {}
) : ViewModel() {
    fun createRoom(roomName: String) {
        onCreateRoom(roomName)
    }
}

class JoinViewModel(
    private val distributedState: DistributedMergeable<RoomSharedState>,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
    private val onJoinRoom: (participantId: UUID) -> Unit = {}
) : ViewModel() {

    val roomSharedState by distributedState.states.collectAsState(scope)
    val participantCount by derivedStateOf { roomSharedState.participants.count() }

    fun joinRoom(participantName: String) {
        val participant = Participant(participantName)
        distributedState.update {
            putParticipant(participant)
        }
        onJoinRoom(participant.uuid)
    }
}

class RoomViewModel(
    val participantId: UUID,
    private val distributedState: DistributedMergeable<RoomSharedState>,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
) : ViewModel() {

    val roomSharedState by distributedState.states.collectAsState(scope)

    override fun leave() {
        distributedState.update {
            removeParticipant(participantId)
        }
    }
}
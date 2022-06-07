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

class JoinPageViewModel(
    private val distributedState: DistributedMergeable<RoomSharedState>,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
    private val onJoinRoom: (participantId: UUID) -> Unit = {}
) : PageViewModel() {

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
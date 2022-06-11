package poker.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import nl.avwie.common.UUID
import poker.sharedstate.Participant
import poker.sharedstate.RoomState

class JoinPageViewModel(
    roomState: MutableState<RoomState>,
    private val onJoinRoom: (participantId: UUID) -> Unit = {}
) : PageViewModel() {

    var state by roomState
    val participantCount by derivedStateOf { state.participants.count() }

    fun joinRoom(participantName: String) {
        val participant = Participant(participantName)
        state = state.putParticipant(participant)
        onJoinRoom(participant.uuid)
    }
}
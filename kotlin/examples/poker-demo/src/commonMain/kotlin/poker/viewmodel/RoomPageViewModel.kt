package poker.viewmodel

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nl.avwie.common.UUID
import poker.sharedstate.RoomState
import kotlin.coroutines.EmptyCoroutineContext


class RoomPageViewModel(
    roomState: MutableState<RoomState>,
    private val participantId: UUID,
    private val invitationURL: String,
    private val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
) : PageViewModel() {

    private var state by roomState

    val name get() = state.name
    val participant get() = state.participants[participantId]
    val participants by derivedStateOf {  state.participants.values.sortedBy { it.name } }
    val canReveal by derivedStateOf { participants.filter { it.isActive }.all { it.score != null } }
    val story get() = state.story
    val revealed get() = state.revealed
    val average by derivedStateOf {
        val avg = participants.filter { it.isActive }.mapNotNull { it.score }.average()
        if (avg.isNaN()) 0.0 else avg
    }

    fun generateInvitation(): String {
        return invitationURL
    }

    fun setStory(story: String) {
        state = state.setStory(story)
    }

    fun setScore(score: Int) {
        participant?.also {
            state = state.updateParticipant(it.uuid) {
                this.setScore(score)
            }
        }
    }

    fun clearAllScores() {
        state = state.participants.keys.fold(state) { s, p ->
            s.updateParticipant(p) {
                setScore(null)
            }
        }.setRevealed(false)
    }

    fun reveal() {
        state = state.setRevealed(true)
    }

    private val remainActiveJob = scope.launch {
        snapshotFlow { state }
            .onEach { updatedState ->
                if (updatedState.participants.contains(participantId) && !updatedState.participants[participantId]!!.isActive) {
                    state = state.updateParticipant(participantId) {
                        setActive()
                    }
                }
            }
            .collect()
    }


    override fun leave() {
        participant?.also {
            println("Removing participant: ${it.name}")
            remainActiveJob.cancel()
            state = state.updateParticipant(it.uuid) {
                setInactive()
            }
        }
    }

    override fun dispose() {
        scope.cancel()
    }
}
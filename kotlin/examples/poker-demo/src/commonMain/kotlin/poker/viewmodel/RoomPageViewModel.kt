package poker.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nl.avwie.common.UUID
import nl.avwie.crdt.convergent.DistributedMergeable
import poker.sharedstate.DistributedMergeableState
import poker.sharedstate.RoomState
import poker.util.collectAsState
import kotlin.coroutines.EmptyCoroutineContext


class RoomPageViewModel(
    roomState: DistributedMergeableState<RoomState>,
    private val participantId: UUID,
    private val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
) : PageViewModel() {

    private var state by roomState

    val name get() = state.name
    val participant get() = state.participants[participantId]
    val participants by derivedStateOf {  state.participants.values.sortedBy { it.name } }
    val story get() = state.story

    fun setStory(story: String) {
        state = state.setStory(story)
    }

    val score get() = participant?.score

    fun setScore(score: Int) {
        participant?.also {
            state = state.updateParticipant(it.uuid) {
                this.setScore(score)
            }
        }
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
            state = state.removeParticipant(it.uuid)
        }
    }

    override fun dispose() {
        scope.cancel()
    }
}
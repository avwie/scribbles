package poker.viewmodel

import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nl.avwie.common.UUID
import nl.avwie.crdt.convergent.DistributedMergeable
import poker.sharedstate.RoomSharedState
import poker.util.collectAsState
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

class RoomPageViewModel(
    private val participantId: UUID,
    private val distributedState: DistributedMergeable<RoomSharedState>,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
) : PageViewModel() {
    private var isLeaving = false

    val roomSharedState by distributedState.states.collectAsState(scope)
    val participant get() = roomSharedState.participants[participantId]

    init {
        distributedState.states.onEach { state ->
            if (!isLeaving && state.participants.contains(participantId) && !state.participants[participantId]!!.isActive) {
                distributedState.update {
                    updateParticipant(participantId) {
                        setActive()
                    }
                }
            }
        }.launchIn(scope)

        /*tickerFlow(5.seconds, initialDelay = 5.seconds)
            .onEach { cleanUpInactiveParticipants() }
            .launchIn(scope)*/
    }

    fun setStory(story: String) {
        distributedState.update {
            setStory(story)
        }
    }

    fun setScore(score: Int?) {
        participant?.also {
            distributedState.update {
                updateParticipant(it.uuid) {
                    setScore(score)
                }
            }
        }
    }

    override fun leave() {
        participant?.also {
            isLeaving = true
            distributedState.update {
                println("Leaving: $participantId")
                removeParticipant(it.uuid)
            }
        }
    }
}
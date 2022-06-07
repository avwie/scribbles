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
    val participantId: UUID,
    private val distributedState: DistributedMergeable<RoomSharedState>,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
) : PageViewModel() {

    val roomSharedState by distributedState.states.collectAsState(scope)

    init {
        roomSharedState.participants[participantId]?.let { participant ->
            if (!participant.isActive) {
                distributedState.update {
                    updateParticipant(participantId) {
                        setActive()
                    }
                }
            }
        }

        tickerFlow(5.seconds, initialDelay = 5.seconds)
            .onEach { cleanUpInactiveParticipants() }
            .launchIn(scope)
    }

    override fun leave() {
        distributedState.update {
            removeParticipant(participantId)
        }
    }

    private fun cleanUpInactiveParticipants() {
        println("Cleanup")
    }
}
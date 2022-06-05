package poker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nl.avwie.common.UUID
import nl.avwie.common.messagebus.MessageBusFactory
import nl.avwie.common.routing.Router
import nl.avwie.common.routing.push
import nl.avwie.common.uuid
import nl.avwie.crdt.convergent.DistributedMergeable
import nl.avwie.crdt.convergent.distributedMergeableOf
import poker.routing.Route
import poker.sharedstate.RoomSharedState
import kotlin.coroutines.EmptyCoroutineContext

class AppViewModel(
    private val router: Router<Route>,
    private val messageBusFactory: MessageBusFactory<String>,
    scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) {
    var activeViewModel by mutableStateOf<ViewModel>(CreateViewModel(::createRoom))
        private set

    private var distributedState: DistributedMergeable<RoomSharedState>? = null

    init {
        router.activeRoute
            .onEach { route -> navigate(route) }
            .launchIn(scope)
    }

    fun createRoom(name: String) {
        router.history.push(Route.Join(uuid(), name).url)
    }

    fun joinRoom(roomId: UUID, roomName: String, participantId: UUID) {
        router.history.push(Route.Room(roomId, roomName, participantId).url)
    }

    fun leave() {
        activeViewModel.leave()
    }

    private fun navigate(route: Route) {
        activeViewModel = when (route) {
            Route.Create -> CreateViewModel(onCreateRoom = ::createRoom)
            is Route.Join -> JoinViewModel(
                onJoinRoom = { joinRoom(route.roomId, route.roomName, it) },
                distributedState = getOrCreateDistributedState(route.roomId, route.roomName)
            )
            is Route.Room -> RoomViewModel(
                participantId = route.participantId,
                distributedState = getOrCreateDistributedState(route.roomId, route.roomName)
            )
            Route.Error -> ErrorViewModel("Page does not exist", router.history.activeLocation.value.toURL())
        }
    }

    private fun getOrCreateDistributedState(roomId: UUID, roomName: String): DistributedMergeable<RoomSharedState> {
        if (distributedState == null) {
            distributedState = distributedMergeableOf(
                initialState = RoomSharedState(roomName),
                messageBus = messageBusFactory.create(roomId.toString())
            )
        }
        // publish the new state for the first time, to trigger sync with others:
        distributedState?.publish()
        return distributedState as DistributedMergeable<RoomSharedState>
    }
}
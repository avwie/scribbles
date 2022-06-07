package poker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nl.avwie.common.UUID
import nl.avwie.common.messagebus.MessageBusFactory
import nl.avwie.common.persistence.KeyValueStore
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
    private val stateCache: KeyValueStore<RoomSharedState>,
    private val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) {
    var activeViewModel by mutableStateOf<PageViewModel>(CreatePageViewModel(::createRoom))
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

    private suspend fun navigate(route: Route) {
        activeViewModel = when (route) {
            Route.Create -> CreatePageViewModel(onCreateRoom = ::createRoom)
            is Route.Join -> JoinPageViewModel(
                onJoinRoom = { joinRoom(route.roomId, route.roomName, it) },
                distributedState = getOrCreateDistributedState(route.roomId, route.roomName)
            )
            is Route.Room -> RoomPageViewModel(
                participantId = route.participantId,
                distributedState = getOrCreateDistributedState(route.roomId, route.roomName)
            )
            Route.Error -> ErrorPageViewModel("Page does not exist", router.history.activeLocation.value.toURL())
        }
    }

    private suspend fun getOrCreateDistributedState(roomId: UUID, roomName: String): DistributedMergeable<RoomSharedState> {
        val initialState = stateCache.get(roomId.toString()) ?: RoomSharedState(roomName)
        if (distributedState == null) {
            distributedState = distributedMergeableOf(
                initialState = initialState,
                messageBus = messageBusFactory.create(roomId.toString())
            )
        }

        distributedState?.also { state ->

            // publish the new state for the first time, to trigger sync with others
            state.publish()

            // update the cache
            state.states.onEach { newState ->
                stateCache.set(roomId.toString(), newState)
            }.launchIn(scope)
        }
        return distributedState as DistributedMergeable<RoomSharedState>
    }
}
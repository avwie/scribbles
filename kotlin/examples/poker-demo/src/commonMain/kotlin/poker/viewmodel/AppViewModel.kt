package poker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nl.avwie.common.UUID
import nl.avwie.common.messagebus.MessageBusFactory
import nl.avwie.common.persistence.KeyValueStore
import nl.avwie.common.routing.Router
import nl.avwie.common.routing.push
import nl.avwie.common.uuid
import poker.routing.Route
import poker.sharedstate.DistributedMergeableState
import poker.sharedstate.RoomState
import poker.sharedstate.distributedMergeableStateOf
import kotlin.coroutines.EmptyCoroutineContext

class AppViewModel(
    private val router: Router<Route>,
    private val messageBusFactory: MessageBusFactory<String>,
    private val stateCache: KeyValueStore<RoomState>,
    private val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) {
    var activeViewModel by mutableStateOf<PageViewModel>(CreatePageViewModel(::createRoom))
        private set

    private var distributedState: DistributedMergeableState<RoomState>? = null

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

    private suspend fun navigate(route: Route) = updateViewModel {
        when (route) {
            Route.Create -> CreatePageViewModel(onCreateRoom = ::createRoom)
            is Route.Join -> JoinPageViewModel(
                onJoinRoom = { joinRoom(route.roomId, route.roomName, it) },
                roomState = getOrCreateDistributedState(route.roomId, route.roomName)
            )
            is Route.Room -> RoomPageViewModel(
                participantId = route.participantId,
                roomState = getOrCreateDistributedState(route.roomId, route.roomName)
            )
            Route.Error -> ErrorPageViewModel("Page does not exist", router.history.activeLocation.value.toURL())
        }
    }

    private suspend fun updateViewModel(block: suspend () -> PageViewModel) {
        block().also {
            activeViewModel.dispose()
            activeViewModel = it
        }
    }

    private suspend fun getOrCreateDistributedState(
        roomId: UUID,
        roomName: String
    ): DistributedMergeableState<RoomState> {
        val initialState = stateCache.get(roomId.toString()) ?: RoomState(roomName)
        if (distributedState == null) {
            distributedState = distributedMergeableStateOf(
                initialState = initialState,
                messageBus = messageBusFactory.create(roomId.toString())
            )

            distributedState?.also { state ->

                // publish the new state for the first time, to trigger sync with others
                state.publish()

                // update the cache
                snapshotFlow {
                    state.value
                }.onEach { newState ->
                    stateCache.set(roomId.toString(), newState)
                }.launchIn(scope)
            }
        }
        return distributedState as DistributedMergeableState<RoomState>
    }
}
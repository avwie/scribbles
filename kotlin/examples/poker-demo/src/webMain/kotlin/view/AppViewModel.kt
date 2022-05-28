package view

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import common.routing.Location
import repository.LocalStorageRepository
import router.Route
import router.Router
import state.Participant
import state.State

class AppViewModel(val router: Router<Route>) {

    var roomViewModel: RoomViewModel? by mutableStateOf(null)
        private set

    init {
        initialize(router.activeRoute)
    }

    fun enterRoom(roomName: String) {
        roomViewModel = RoomViewModel(roomName)
        router.history.push(Location.parse(Route.Join(roomName).href))
    }

    fun enterName(participantName: String) {
        roomViewModel?.also {
            it.setParticipant(participantName)
            router.history.push(Location.parse(Route.Room(it.state.name, it.participant!!.uuid).href))
        }

    }

    fun leave() {
        roomViewModel?.leave()
    }

    private fun initialize(route: Route) {
        console.log("Initializing on route: ", router.activeRoute)
        when (route) {
            is Route.Join -> enterRoom(route.room)
            is Route.Room -> {}
            else -> {}
        }
    }
}

class RoomViewModel(roomName: String) {

    var state: State by mutableStateOf(State(roomName))
        private set

    var participant: Participant? by mutableStateOf(null)
        private set

    val participants get() = state.participants.values

    private val repository = LocalStorageRepository(roomName.lowercase(), onStorageUpdate = ::merge)

    init {
        repository.retrieve()?.also {
            merge(it)
        }
        repository.store(state)
    }

    fun setParticipant(name: String) {
        participant = Participant(name)
        updateState { current -> current.putParticipant(participant!!) }
    }

    fun leave() {
        participant?.also {
            updateState { current -> current.removeParticipant(it.uuid) }
        }
    }

    private fun merge(incoming: State) {
        state = state.merge(incoming)
    }

    private fun updateState(block: (current: State) -> State) {
        state = block(state)
        repository.store(state)
    }
}
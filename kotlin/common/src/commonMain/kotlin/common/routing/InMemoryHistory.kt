package common.routing

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class InMemoryHistory(initialLocation: Location) : History {

    private val back = ArrayDeque<Location>()
    private val forward = ArrayDeque<Location>()

    private val _activeLocation = mutableStateOf(initialLocation)
    override val activeLocation: State<Location> = _activeLocation

    init {
        back.addLast(initialLocation)
    }

    override fun push(location: Location) {
        forward.clear()
        back.addLast(activeLocation.value)
        _activeLocation.value = location
    }

    override fun forward() {
        forward.removeFirstOrNull()?.let {
            back.addLast(activeLocation.value)
            _activeLocation.value = it
        }
    }

    override fun back() {
        back.removeLastOrNull()?.let {
            forward.addFirst(activeLocation.value)
            _activeLocation.value = it
        }
    }

    override fun peekForward(): Location? = forward.firstOrNull()
    override fun peekBack(): Location? = back.lastOrNull()
}
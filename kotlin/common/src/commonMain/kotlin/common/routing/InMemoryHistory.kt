package common.routing

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class InMemoryHistory(initialLocation: Location) : History {

    private var locations = mutableListOf(initialLocation)
    private var index = 0

    private var _activeLocation = mutableStateOf(locations[index])
    override val activeLocation: State<Location> = _activeLocation

    override fun push(location: Location) {
        locations = locations.subList(0, index + 1)
        locations.add(++index, location)
        updateState()
    }

    override fun forward() {
        if (index < locations.size - 1) {
            index += 1
            updateState()
        }
    }

    override fun back() {
        if (index > 0) {
            index -= 1
            updateState()
        }
    }

    private fun updateState() {
        _activeLocation.value = locations[index]
    }
}
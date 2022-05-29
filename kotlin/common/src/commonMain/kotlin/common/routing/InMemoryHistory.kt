package common.routing

import kotlinx.coroutines.flow.*

class InMemoryHistory(initialLocation: Location) : History {

    constructor(
        initialPathName: String,
        initialQuery: String? = null,
        initialHash: String? = null
    ): this(Location(initialPathName, initialQuery, initialHash))

    private val back = ArrayDeque<Location>()
    private val forward = ArrayDeque<Location>()

    private val _activeLocation = MutableStateFlow(initialLocation)
    override val activeLocation: StateFlow<Location> = _activeLocation.asStateFlow()

    init {
        back.addLast(initialLocation)
    }

    override fun push(location: Location) {
        forward.clear()
        back.addLast(_activeLocation.value)
        _activeLocation.update { location }
    }

    override fun forward() {
        forward.removeFirstOrNull()?.let {
            back.addLast(_activeLocation.value)
            _activeLocation.update { it }
        }
    }

    override fun back() {
        back.removeLastOrNull()?.let {
            forward.addFirst(_activeLocation.value)
            _activeLocation.update { it }
        }
    }

    override fun peekForward(): Location? = forward.firstOrNull()
    override fun peekBack(): Location? = back.lastOrNull()
}
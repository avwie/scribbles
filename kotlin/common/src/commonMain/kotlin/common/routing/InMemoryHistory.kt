package common.routing

import common.mapSync
import common.uuid
import kotlinx.coroutines.flow.*

class InMemoryHistory(initialLocation: Location) : History {

    data class Entry(val id: common.UUID, val location: Location)

    constructor(
        initialPathName: String,
        initialQuery: String? = null,
        initialHash: String? = null
    ): this(Location(initialPathName, initialQuery, initialHash))

    private val back = ArrayDeque<Entry>()
    private val forward = ArrayDeque<Entry>()

    private val _activeEntry = MutableStateFlow(Entry(uuid(), initialLocation))
    val activeEntry: StateFlow<Entry> = _activeEntry.asStateFlow()

    override val activeLocation: StateFlow<Location> = activeEntry.mapSync { it.location }

    init {
        back.addLast(_activeEntry.value)
    }

    override fun push(location: Location) {
        forward.clear()
        back.addLast(_activeEntry.value)
        _activeEntry.update { Entry(uuid(), location) }
    }

    override fun forward() {
        forward.removeFirstOrNull()?.let { entry ->
            back.addLast(_activeEntry.value)
            _activeEntry.update { entry }
        }
    }

    override fun back() {
        back.removeLastOrNull()?.let { entry ->
            forward.addFirst(_activeEntry.value)
            _activeEntry.update { entry }
        }
    }

    override fun peekForward(): Location? = peekForwardEntry()?.location
    override fun peekBack(): Location? = peekBackEntry()     ?.location

    fun peekForwardEntry(): Entry? = forward.firstOrNull()
    fun peekBackEntry(): Entry? = back.lastOrNull()
}
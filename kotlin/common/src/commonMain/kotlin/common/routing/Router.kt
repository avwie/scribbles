package common.routing

import common.mapSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Router<R>(
    private val history: History,
    private val routing: Routing<R>,
) {
    val activeRoute: StateFlow<R> = history.activeLocation.mapSync { location -> routing.getRoute(location) }
}
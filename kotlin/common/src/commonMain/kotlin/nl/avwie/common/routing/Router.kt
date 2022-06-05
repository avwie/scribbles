package nl.avwie.common.routing

import kotlinx.coroutines.flow.*
import nl.avwie.common.mapSync

class Router<R>(
    val history: History,
    val routing: Routing<R>,
) {
    val activeRoute: StateFlow<R> = history.activeLocation.mapSync { location -> routing.getRoute(location) }
}
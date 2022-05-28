package router

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import externals.history.History

class Router<R>(val history: History, val routing: Routing<R>) {

    var activeRoute by mutableStateOf(routing.getRoute(history.location))
        private set

    var activeLocation by mutableStateOf(history.location)
        private set

    init {
        history.listen { update ->
            activeRoute = routing.getRoute(update.location)
            activeLocation = update.location
        }
    }
}
package router

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import common.routing.History

class Router<R>(val history: History, val routing: Routing<R>) {
    val activeRoute by derivedStateOf { routing.getRoute(history.activeLocation.value) }
}
package poker.routing

import nl.avwie.common.routing.*

sealed class Route(val url: String) {
    object Create : Route("/")
    object Error : Route("/404")

    data class Join(
        val roomId: nl.avwie.common.UUID,
        val roomName: String
    ) : Route("/join/$roomId/$roomName")

    data class Room(
        val roomId: nl.avwie.common.UUID,
        val roomName: String,
        val participantId: nl.avwie.common.UUID
    ) : Route("/room/$roomId/$roomName/$participantId")
}

val Routing = createRouting<Route>(Route.Error) {
    matchPathName("/") { Route.Create }

    matchFragments(TextMatch("join"), UUID, Text) { _, roomId, roomName ->
        Route.Join(roomId, roomName)
    }

    matchFragments(TextMatch("room"), UUID, Text, UUID) { _, roomId, roomName, participantId ->
        Route.Room(roomId, roomName, participantId)
    }
}
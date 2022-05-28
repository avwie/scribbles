package router

import common.UUID

sealed class Route(val href: String) {
    object Error : Route("/404")
    object LandingPage : Route("/")
    data class Join(val room: String) : Route("/$room")
    data class Room(val room: String, val participantId: UUID) : Route("/$room/$participantId")
}

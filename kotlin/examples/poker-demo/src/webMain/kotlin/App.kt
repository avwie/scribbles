import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import externals.history.createBrowserHistory
import kotlinx.browser.window
import org.jetbrains.compose.web.renderComposable
import router.Route
import router.Router
import router.createRouting
import router.fragment.Text
import router.fragment.UUID
import ui.ErrorPage
import ui.JoinPage
import ui.LandingPage
import ui.Roompage
import view.AppViewModel

val routing = createRouting<Route>(Route.Error) {
    matchPathName("/") { Route.LandingPage }
    matchFragments(Text) { room -> Route.Join(room)}
    matchFragments(Text, UUID) { room, uuid -> Route.Room(room, uuid) }
}

val LocalRouter = staticCompositionLocalOf{ Router(createBrowserHistory(), routing) }

fun main() {
    renderComposable("root") {
        val router = LocalRouter.current
        val model = remember { AppViewModel(router) }

        LaunchedEffect(window) {
            window.onbeforeunload = {
                model.leave()
                null
            }
        }

        when (val currentRoute = router.activeRoute) {
            is Route.LandingPage -> LandingPage(onEnterRoom = { roomName -> model.enterRoom(roomName) })
            is Route.Join -> JoinPage(
                roomName = currentRoute.room,
                activeParticipants = model.roomViewModel?.participants?.size ?: 0,
                onEnterName = { name -> model.enterName(name) }
            )
            is Route.Room -> Roompage()
            else -> ErrorPage(router.activeLocation.pathname)
        }
    }
}
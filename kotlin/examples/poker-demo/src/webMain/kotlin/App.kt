import org.jetbrains.compose.web.renderComposable

/*val routing = createRouting<Route>(Route.Error) {
    matchPathName("/") { Route.LandingPage }
    matchFragments(Text) { room -> Route.Join(room)}
    matchFragments(Text, UUID) { room, uuid -> Route.Room(room, uuid) }
}

val history = BrowserHistory()
val LocalHistory = staticCompositionLocalOf { history }*/

fun main() {
    renderComposable("root") {

    }
}
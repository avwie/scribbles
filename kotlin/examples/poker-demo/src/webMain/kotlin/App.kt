import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import nl.avwie.common.messagebus.BrowserLocalStorageMessageBus
import nl.avwie.common.routing.BrowserHistory
import kotlinx.browser.window
import nl.avwie.common.messagebus.MessageBusFactory
import nl.avwie.common.persistence.SerializingKeyValueStore
import nl.avwie.common.persistence.browserSessionStorage
import nl.avwie.common.routing.Router
import org.jetbrains.compose.web.renderComposable
import poker.routing.Routing
import poker.sharedstate.RoomState
import poker.viewmodel.*
import ui.*

val router = Router(BrowserHistory(), Routing)

val messageBusFactory = MessageBusFactory {
    BrowserLocalStorageMessageBus(topic = it)
}

val stateCache = SerializingKeyValueStore<RoomState>(browserSessionStorage())

fun main() {
    renderComposable("root") {
        val appViewModel = remember { AppViewModel(router, messageBusFactory, stateCache) }

        LaunchedEffect(Unit) {
            window.onbeforeunload = {
                appViewModel.leave()
                null
            }
        }

        FullPageCenter {
            when (val currentViewModel = appViewModel.activeViewModel) {
                is ErrorPageViewModel -> ErrorPage(currentViewModel.title, currentViewModel.message)
                is CreatePageViewModel -> CreatePage(currentViewModel)
                is JoinPageViewModel -> JoinPage(currentViewModel)
                is RoomPageViewModel -> RoomPage(currentViewModel)
            }
        }
    }
}
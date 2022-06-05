import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import nl.avwie.common.messagebus.BrowserLocalStorageMessageBus
import nl.avwie.common.routing.BrowserHistory
import kotlinx.browser.window
import nl.avwie.common.messagebus.MessageBusFactory
import nl.avwie.common.routing.Router
import org.jetbrains.compose.web.renderComposable
import poker.routing.Routing
import poker.viewmodel.*
import ui.CreatePage
import ui.ErrorPage
import ui.FullPageCenter
import ui.JoinPage

val router = Router(BrowserHistory(), Routing)
val messageBusFactory = MessageBusFactory {
    BrowserLocalStorageMessageBus(topic = it)
}

fun main() {
    renderComposable("root") {
        val appViewModel = remember { AppViewModel(router, messageBusFactory) }

        LaunchedEffect(Unit) {
            window.onbeforeunload = {
                appViewModel.leave()
                null
            }
        }

        FullPageCenter {
            when (val currentViewModel = appViewModel.activeViewModel) {
                is ErrorViewModel -> ErrorPage(currentViewModel.title, currentViewModel.message)
                is CreateViewModel -> CreatePage(currentViewModel)
                is JoinViewModel -> JoinPage(currentViewModel)
                is RoomViewModel -> {}
            }
        }
    }
}
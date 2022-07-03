import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import nl.avwie.common.messagebus.BrowserLocalStorageMessageBus
import nl.avwie.common.routing.BrowserHistory
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nl.avwie.common.messagebus.MessageBusFactory
import nl.avwie.common.persistence.SerializingKeyValueStore
import nl.avwie.common.persistence.browserSessionStorage
import nl.avwie.common.routing.Router
import nl.avwie.common.tickerFlow
import nl.avwie.common.uuid
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.BroadcastChannel
import poker.routing.Routing
import poker.sharedstate.RoomState
import poker.util.collectAsState
import poker.viewmodel.*
import ui.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

/*val router = Router(BrowserHistory(), Routing)

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
}*/

fun main() {
    val id = uuid()
    val channel = BroadcastChannel("messaging")
    var i = 0
    channel.onmessage = { event -> console.log("Received: ${event.data}")}

    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        tickerFlow(1.seconds).onEach {
            val message = "$id: ${++i}"
            console.log("Sending: $message")
            channel.postMessage(message)
        }.collect()
    }
}
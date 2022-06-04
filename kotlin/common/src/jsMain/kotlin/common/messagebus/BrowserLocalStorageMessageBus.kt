package common.messagebus

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.w3c.dom.set

class BrowserLocalStorageMessageBus(
    val topic: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : MessageBus<String> {

    private val _messages = MutableSharedFlow<String>()
    override val messages: SharedFlow<String> = _messages

    init {
        window.onstorage = { event ->
            event.newValue?.also { message ->
                scope.launch {
                    _messages.emit(message)
                }
            }
        }
    }

    override suspend fun send(item: String) {
        window.localStorage[topic] = item
        _messages.emit(item)
    }
}
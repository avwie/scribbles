package nl.avwie.examples.crdt

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element
import org.w3c.dom.events.MouseEvent

val DocumentMouseEvents : Flow<MouseEvent> = MutableSharedFlow<MouseEvent>().also { flow ->
    val scope = CoroutineScope(Dispatchers.Default)
    document.addEventListener("click", {
        when (it) {
            is MouseEvent -> {
                scope.launch {
                    flow.emit(it)
                }
            }
        }
    })
}

@Composable fun onDocumentMouseEvent(block: (MouseEvent) -> Unit) {
    LaunchedEffect(Unit) {
        DocumentMouseEvents
            .onEach { block(it) }
            .launchIn(this)
    }
}

fun <T : Element> AttrsScope<T>.hoverState(state: MutableState<Boolean>) {
    onMouseEnter { state.value = true }
    onMouseLeave { state.value = false }
}

fun <T : Element> AttrsScope<T>.refState(state: MutableState<T?>) {
    ref {
        state.value = it
        onDispose {  }
    }
}
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import nl.avwie.common.messagebus.asMessageBus
import nl.avwie.common.messagebus.deserialize
import nl.avwie.crdt.convergent.asStateFlow
import nl.avwie.crdt.convergent.sync
import nl.avwie.crdt.convergent.mergeableDistantPastValueOf
import nl.avwie.crdt.convergent.mergeableValueOf
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.BroadcastChannel
import kotlin.coroutines.EmptyCoroutineContext

fun main() {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val channel = BroadcastChannel("updates").asMessageBus(scope)
    val states = mergeableDistantPastValueOf(0).asStateFlow()
    states.sync(channel.deserialize(scope), scope)

    renderComposable("root") {
        val counter by states.collectAsState()

        Header("Current value: ${counter.value}")
        Button("Increase") { states.update { mergeableValueOf(it.value + 1) } }
        Button("Decrease") { states.update { mergeableValueOf(it.value - 1) } }
    }
}

@Composable fun Header(title: String) = H1 { Text(title) }
@Composable fun Button(label: String, onClick: () -> Unit) =
    Button(
        attrs = {
            classes("btn", "btn-primary", "mx-2")
            onClick { onClick() }
        }
    ) { Text(label) }
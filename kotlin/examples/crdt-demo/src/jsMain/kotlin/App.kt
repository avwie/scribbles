import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import nl.avwie.crdt.convergent.asStateFlow
import nl.avwie.crdt.convergent.broadcast
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
    val channel = BroadcastChannel("updates")
    val states = mergeableDistantPastValueOf(0).asStateFlow()
    states.broadcast(channel, scope)

    renderComposable("root") {
        val counter by states.collectAsState().value

        H1 { Text("Current value: $counter") }

        Button(attrs = {
            classes("btn", "btn-primary", "mx-2")
            onClick { states.update { mergeableValueOf(it.value + 1) } }
        }) { Text("Increase") }

        Button(attrs = {
            classes("btn", "btn-primary", "mx-2")
            onClick { states.update { mergeableValueOf(it.value - 1) } }
        }) { Text("Decrease") }
    }
}
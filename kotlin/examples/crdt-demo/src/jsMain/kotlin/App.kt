import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import nl.avwie.common.messagebus.asMessageBus
import nl.avwie.common.messagebus.deserialize
import nl.avwie.crdt.convergent.MergeableStateFlow
import nl.avwie.crdt.convergent.asStateFlow
import nl.avwie.crdt.convergent.sync
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.BroadcastChannel

fun main() {
    val scope = CoroutineScope(Dispatchers.Default)
    val channel = BroadcastChannel("updates")
            .asMessageBus(scope)
            .deserialize<TodoList>(scope)

    val todoListFlow = TodoList("New list").asStateFlow()
    todoListFlow.sync(channel, scope)

    renderComposable("root") {
        App(todoListFlow)
    }
}

@Composable fun App(todoListFlow: MergeableStateFlow<TodoList>) {
    val todoList = remember { todoListFlow.collectAsState() }

}
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nl.avwie.common.messagebus.asMessageBus
import nl.avwie.common.messagebus.deserialize
import nl.avwie.crdt.convergent.MergeableStateFlow
import nl.avwie.crdt.convergent.asStateFlow
import nl.avwie.crdt.convergent.sync
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.BroadcastChannel

fun main() {
    val scope = CoroutineScope(Dispatchers.Default)
    val channel = BroadcastChannel("updates")
        .asMessageBus(scope)
        .also { jsonBus ->
            jsonBus.messages.onEach {
                console.log(it)
            }.launchIn(scope)
        }
        .deserialize<TodoList>(scope)

    val todoListFlow = TodoList("New list").asStateFlow().sync(channel, scope)
    renderComposable("root") {
        App(todoListFlow)
    }
}

@Composable fun App(todoListFlow: MergeableStateFlow<TodoList>) {
    val scope = rememberCoroutineScope()
    val (todoList, updateTodoList) = remember { todoListFlow.collectAsMutableState(scope) }

    Title(todoList.name)
    Button("Foo", onClick = { updateTodoList { it.setName(it.name.reversed()) } } )
}

@Composable fun Title(value: String) {
    H1({ classes("text-primary") }) { Text(value) }
}

@Composable fun Button(label: String, onClick: () -> Unit = {}) {
    org.jetbrains.compose.web.dom.Button({
        classes("btn", "btn-primary")
        onClick { onClick() }
    }) {
        Text(label)
    }
}
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
        Centered {
            ResponsiveContainer {
                App(todoListFlow)
            }
        }
    }
}

@Composable fun App(todoListFlow: MergeableStateFlow<TodoList>) {
    val scope = rememberCoroutineScope()
    val (todoList, updateTodoList) = remember { todoListFlow.collectAsMutableState(scope) }

    Row {
        Col(12) {
            EditableTitle(todoList.name, onTitleUpdate = { title -> updateTodoList { it.setName(title) } })
        }
    }
}



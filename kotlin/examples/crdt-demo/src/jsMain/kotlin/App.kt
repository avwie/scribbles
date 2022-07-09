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
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.dom.P
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

    val todoListFlow = TodoList("New list")
        .addItem("Foo")
        .addItem("Bar")
        .asStateFlow().sync(channel, scope)
    renderComposable("root") {
        Style(AppStyleSheet)
        FullPageCentered {
            App(todoListFlow)
        }
    }
}

@Composable fun App(todoListFlow: MergeableStateFlow<TodoList>) {
    val scope = rememberCoroutineScope()
    val (todoList, updateTodoList) = remember { todoListFlow.collectAsMutableState(scope) }

    RowContainer {
        Input(todoList.name,
            placeholder = "Enter your list name and press enter...",
            onSubmit = { title -> updateTodoList { it.setName(title) }},
            attrs = { style { fontWeight(600) }}
        )
        Separator()
        Items(
            items = todoList.items,
            onItemChecked = { item, value ->
                updateTodoList {
                    if (value) it.finishItem(item)
                    else it.unfinishItem(item)
                }
            },
            onItemDeleted = { item -> updateTodoList{ it.removeItem(item) }}
        )
        Separator()
        Input("",
            placeholder = "Enter new item and press enter...",
            onSubmit = { item -> updateTodoList { it.addItem(item) }},
            resetAfterSubmit = true
        )
    }
}



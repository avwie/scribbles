package nl.avwie.examples.crdt

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nl.avwie.common.messagebus.ServerSentEventBus
import nl.avwie.common.messagebus.deserialize
import nl.avwie.crdt.convergent.MergeableStateFlow
import nl.avwie.crdt.convergent.asStateFlow
import nl.avwie.crdt.convergent.sync
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable

fun main() {
    val scope = CoroutineScope(Dispatchers.Default)
    val sseBus = ServerSentEventBus(
        publishEndpoint = "http://localhost:8080/publish",
        subscribeEndpoint = "http://localhost:8080/subscribe",
        scope = scope
    )

    val bus = sseBus
        .also { jsonBus ->
            jsonBus.messages.onEach {
                console.log("Received: ", it)
            }.launchIn(scope)
        }
        .deserialize<TodoList>(scope)

    val todoListFlow = TodoList("New list")
        .asStateFlow()
        .sync(bus, scope)

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
        Title("Todo List")
        MainPanel {
            RowContainer {
                FancyInput(todoList.name,
                    placeholder = "Enter your list name and press enter...",
                    onSubmit = { title -> updateTodoList(todoList.setName(title)) },
                    attrs = { classes(AppStyleSheet.radiusTop, AppStyleSheet.nameInput) }
                )
                Separator()
                Items(
                    items = todoList.items,
                    onItemChecked = { item, value ->
                        if (value) updateTodoList(todoList.finishItem(item))
                        else updateTodoList(todoList.unfinishItem(item))
                    },
                    onItemDeleted = { item -> updateTodoList(todoList.removeItem(item)) }
                )
                if (todoList.items.isNotEmpty()) {
                    Separator()
                }
                FancyInput(
                    "",
                    placeholder = "Enter new item and press enter...",
                    onSubmit = { item -> updateTodoList(todoList.addItem(item)) },
                    resetAfterSubmit = true,
                    attrs = { classes( AppStyleSheet.radiusBottom) }
                )
            }
        }
    }
}



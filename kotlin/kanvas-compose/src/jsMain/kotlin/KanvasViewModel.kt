import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class KanvasViewModel(initialModel: Model) : Dispatcher {

    var model by mutableStateOf(initialModel)
        private set

    override fun dispatch(msg: Message) {
        model = model.copy(
            items = updateItems(model.items, msg)
        )
    }

    private fun updateItems(items: Iterable<Item>, msg: Message): List<Item> = items.map { item ->
        when {
            msg is HasTargetId && msg.targetId == item.id -> updateItem(item, msg)
            else -> item
        }
    }

    private fun updateItem(item: Item, msg: Message) = when (msg) {
        is ItemClicked -> item.copy(selected = !item.selected)
        is ItemMouseDown -> item.copy(dragState = DragState.DeadZone(msg.position))
    }
}
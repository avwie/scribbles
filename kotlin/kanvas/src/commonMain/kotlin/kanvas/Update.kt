package kanvas

fun update(model: Model, message: Message): Model = model.copy(
    items = updateItems(model.items, message)
)

private fun updateItems(items: List<Item>, message: Message): List<Item> = items.map { item ->
    when (message) {
        is MouseClick -> when (message.entityId) {
            item.entityId -> item.copy(selected = !item.selected)
            else -> item
        }
    }
}
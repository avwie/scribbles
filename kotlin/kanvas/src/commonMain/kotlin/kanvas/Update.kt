package kanvas

fun update(model: Model, message: Message, context: Context): Model = with (context) {
    model.copy(
        items = updateItems(model.items, message)
    )
}

private fun Context.updateItems(items: List<Item>, message: Message): List<Item> = items.map { item ->
    when (message) {
        is MouseClick -> when (message.entityId) {
            item.entityId -> item.copy(selected = !item.selected)
            else -> item
        }
    }
}
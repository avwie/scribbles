package nl.avwie.examples.crdt

import nl.avwie.crdt.convergent.*

@kotlinx.serialization.Serializable
data class TodoList(
    private val _name: MergeableValue<String>,
    private val _items: MergeableMap<String, Boolean>
) : Mergeable<TodoList>  {

    constructor(name: String) : this(mergeableDistantPastValueOf(name), mergeableMapOf())

    val name: String by _name
    val items: Map<String, Boolean> = _items

    fun setName(name: String): TodoList = copy(
        _name = mergeableValueOf(name)
    )

    fun addItem(item: String): TodoList = copy(
        _items = _items.put(item, false)
    )

    fun removeItem(item: String): TodoList = copy(
        _items = _items.remove(item)
    )

    fun finishItem(item: String): TodoList = copy(
        _items = if (_items.containsKey(item)) _items.put(item, true) else _items
    )

    fun unfinishItem(item: String): TodoList = copy(
        _items = if (_items.containsKey(item)) _items.put(item, false) else _items
    )

    override fun merge(other: TodoList): TodoList {
        return TodoList(
            _name = _name.merge(other._name),
            _items = _items.merge(other._items)
        )
    }
}
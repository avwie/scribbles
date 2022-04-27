package kanvas

import common.UUID
import nl.avwie.vdom.EnvironmentMouseEventData
import nl.avwie.vdom.Materializable

sealed interface Message

interface MouseEventData {
    val x: Int
    val y: Int
}

sealed class MouseEvent : Message, MouseEventData, Materializable {

    private var _x: Int = 0
    private var _y: Int = 0

    init {
        materialize()
    }

    override val x: Int
        get() = _x

    override val y: Int
        get() = _y

    final override fun materialize() {
        _x = EnvironmentMouseEventData.x()
        _y = EnvironmentMouseEventData.y()
    }
}
data class MouseClick(val entityId: UUID) : MouseEvent()
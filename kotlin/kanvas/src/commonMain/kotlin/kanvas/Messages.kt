package kanvas

import common.UUID

sealed interface Message

sealed interface MouseEvent : Message { val x: Double; val y: Double }
data class MouseMove(override val x: Double, override val y: Double): MouseEvent
data class MouseIn(override val x: Double, override val y: Double): MouseEvent
data class MouseOut(override val x: Double, override val y: Double): MouseEvent

data class MouseHover(val entityId: UUID, override val x: Double, override val y: Double): MouseEvent
data class MouseClick(val entityId: UUID, override val x: Double, override val y: Double): MouseEvent
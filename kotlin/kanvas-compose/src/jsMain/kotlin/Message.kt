import common.UUID

sealed interface Message
sealed interface HasTargetId : Message {
    val targetId: UUID
}

data class ItemClicked(override val targetId: UUID, val position: Position.Absolute) : Message, HasTargetId
data class ItemMouseDown(override val targetId: UUID, val position: Position.Absolute) : Message, HasTargetId
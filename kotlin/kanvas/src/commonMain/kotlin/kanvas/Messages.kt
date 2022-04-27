package kanvas

import common.UUID

sealed interface Message
sealed interface MouseEvent : Message
data class MouseClick(val entityId: UUID) : MouseEvent
import androidx.compose.web.events.SyntheticMouseEvent
import org.w3c.dom.events.MouseEvent

fun MouseEvent.toPosition() = Position.Absolute(offsetX, offsetY)
fun SyntheticMouseEvent.toPosition() = Position.Absolute(offsetX, offsetY)
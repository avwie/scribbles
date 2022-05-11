import androidx.compose.runtime.CompositionLocalProvider
import common.uuid
import org.jetbrains.compose.web.renderComposable

private fun initialState(): Model = Model(
    grid = Grid(
        visible = true,
        rows = 5,
        cols = 5,
        cellWidth = 150,
        cellHeight = 100,
        cellScale = 0.8
    ),
    items = listOf(
        Item(
            id = uuid(),
            position = Position.Grid(0, 0),
        ),
        Item(
            id = uuid(),
            position = Position.Grid(2, 2),
        )
    )
)

fun main() {

    val ctx = KanvasViewModel(initialState())
    renderComposable("root") {
        CompositionLocalProvider(LocalDispatcher provides ctx) {
            Kanvas(ctx.model)
        }
    }
}
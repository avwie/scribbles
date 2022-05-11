@file:OptIn(ExperimentalComposeWebSvgApi::class)

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameMillis
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.svg.*
import org.w3c.dom.svg.SVGElement

@Composable fun Kanvas(model: Model) {
    with(model.grid) {
        Svg(attrs = {
            attr("width", width().toString())
            attr("height", height().toString())
            attr("viewBox", "0 0 ${width()} ${height()}")
        }) {
            Layer("grid") {
                Grid(model.grid)
            }

            Layer("items") {
                Items(model.items, model.grid)
            }
        }
    }
}

@Composable fun ElementScope<SVGElement>.Layer(name: String, contentBuilder: ContentBuilder<SVGElement>) {
    G(attrs = {
        classes("layer", name)
    }) {
        contentBuilder()
    }
}

@Composable fun ElementScope<SVGElement>.Grid(grid: Grid) {
    if (grid.visible) {

        repeat(grid.rows + 1) { i ->
            Line(x1 = 0, x2 = grid.width(), y1 = i * grid.cellHeight, y2 = i * grid.cellHeight, attrs = {
                attr("stroke", "black")
                attr("stroke-dasharray", "2")
            })
        }

        repeat(grid.cols + 1) { i ->
            Line(y1 = 0, y2 = grid.height(), x1 = i * grid.cellWidth, x2 = i * grid.cellWidth, attrs = {
                attr("stroke", "black")
                attr("stroke-dasharray", "2")
            })
        }
    }
}

@Composable fun ElementScope<SVGElement>.Items(items: Iterable<Item>, grid: Grid) {
    val width = grid.cellWidth * grid.cellScale
    val height = grid.cellHeight * grid.cellScale
    val dispatcher = LocalDispatcher.current

    items.forEach { item ->
        val (x: Double, y: Double) = when (item.position) {
            is Position.Absolute -> item.position.x to item.position.y
            is Position.Grid -> item.position.col * grid.cellWidth.toDouble() to item.position.row * grid.cellHeight.toDouble()
        }

        G(attrs = {
            classes("item")
            attr("data-item-id", item.id.toString())
        }) {
            Rect(
                x = x + grid.offsetX(),
                y = y + grid.offsetY(),
                width = width,
                height = height,
                attrs = {
                    attr("draggable", "true")
                    attr("rx", grid.offsetX().toString())
                    attr("fill", if (item.selected) "#ff0000" else "#00ff00")
                    onClick { event -> dispatcher.dispatch(ItemClicked(item.id, event.toPosition())) }
                }
            )
        }
    }
}
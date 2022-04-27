package kanvas

import nl.avwie.vdom.Node
import nl.avwie.vdom.svg

typealias View = Node.BuilderScope<Message>
typealias Block = View.() -> Unit

fun render(model: Model) = with (model.grid) {
    svg<Message> {
        attr("width" by width())
        attr("height" by height())
        attr("viewBox" by "0 0 ${width()} ${height()}")

        grid(model.grid)
        items(model.items, model.grid)
    }
}

fun View.layer(name: String, block: Block) = "g" ("class" by "layer $name") { block() }

fun View.grid(grid: Grid) = layer("grid") {
    if (grid.visible) {
        repeat(grid.rows + 1) { i ->
            "line"(
                "x1" by 0, "x2" by grid.width(),
                "y1" by i * grid.cellHeight, "y2" by i * grid.cellHeight,
                "stroke" by "black", "stroke-dasharray" by 2
            )
        }

        repeat(grid.cols + 1) { i ->
            "line"(
                "x1" by i * grid.cellWidth, "x2" by i * grid.cellWidth,
                "y1" by 0, "y2" by grid.height(),
                "stroke" by "black", "stroke-dasharray" by 2
            )
        }
    }
}

fun View.items(items: List<Item>, grid: Grid) = layer("items") {
    val width = grid.cellWidth * grid.cellScale
    val height = grid.cellHeight * grid.cellScale

    items.forEach { item ->
        val (x: Double, y: Double) = when (item.position) {
            is Position.Absolute -> item.position.x to item.position.y
            is Position.Grid -> item.position.col * grid.cellWidth.toDouble() to item.position.row * grid.cellHeight.toDouble()
        }

        "g" {
            event("click", MouseClick(item.entityId))
            
            "rect"(
                "x" by x + grid.offsetX(), "y" by y + grid.offsetY(),
                "width" by width, "height" by height,
                "rx" by grid.offsetX(),
                "fill" by (if (item.selected) "#ff0000" else "#00ff00")
            )
        }
    }
}
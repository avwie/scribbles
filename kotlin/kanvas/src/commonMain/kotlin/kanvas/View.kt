package kanvas

import nl.avwie.vdom.Node
import nl.avwie.vdom.svg

typealias View = Node.BuilderScope<Message>
typealias Block = View.() -> Unit

fun render(model: Model) = with (model.grid) {
    svg<Message>(
        "width" to width(),
        "height" to height(),
        "viewBox" to "0 0 ${width()} ${height()}"
    ) {
        grid(model.grid)
        items(model.items, model.grid)
    }
}

fun View.layer(name: String, block: Block) = "g" ("class" to "layer $name") { block() }

fun View.grid(grid: Grid) = layer("grid") {
    if (grid.visible) {
        repeat(grid.rows + 1) { i ->
            "line"(
                "x1" to 0, "x2" to grid.width(),
                "y1" to i * grid.cellHeight, "y2" to i * grid.cellHeight,
                "stroke" to "black", "stroke-dasharray" to 2
            )
        }

        repeat(grid.cols + 1) { i ->
            "line"(
                "x1" to i * grid.cellWidth, "x2" to i * grid.cellWidth,
                "y1" to 0, "y2" to grid.height(),
                "stroke" to "black", "stroke-dasharray" to 2
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
                "x" to x + grid.offsetX(), "y" to y + grid.offsetY(),
                "width" to width, "height" to height,
                "rx" to grid.offsetX(),
                "fill" to (if (item.selected) "#ff0000" else "#00ff00")
            )
        }
    }
}
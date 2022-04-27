package kanvas

import common.UUID

data class Model(
    val grid: Grid,
    val items: List<Item>
)

data class Grid(
    val visible: Boolean,
    val rows: Int,
    val cols: Int,
    val cellHeight: Int,
    val cellWidth: Int,
    val cellScale: Double
) {
    fun width() = cols * cellWidth
    fun height() = rows * cellHeight
    fun offsetX() = cellWidth * (1 - cellScale) / 2
    fun offsetY() = cellHeight * (1 - cellScale) / 2
}

data class Item(
    val entityId: UUID,
    val position: Position,
    val selected: Boolean = false
)

sealed interface Position {
    data class Grid(val row: Int, val col: Int): Position
    data class Absolute(val x: Double, val y: Double): Position
}
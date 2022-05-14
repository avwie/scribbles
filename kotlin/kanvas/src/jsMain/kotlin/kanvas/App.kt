package kanvas

import common.uuid
import kotlinx.browser.document
import nl.avwie.vdom.BrowserDocumentTarget
import nl.avwie.vdom.MVU

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
            entityId = uuid(),
            position = Position.Grid(0, 0),
        ),
        Item(
            entityId = uuid(),
            position = Position.Grid(2, 2),
        )
    )
)

fun main() {
    val container = document.getElementById("container")!!
    val target = BrowserDocumentTarget(container)
    val mvu = MVU(
        target = target,
        context = BrowserContext(),
        initialState = initialState(),
        render = ::render,
        update = ::update
    )
}
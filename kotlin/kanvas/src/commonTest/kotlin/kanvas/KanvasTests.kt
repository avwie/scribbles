package kanvas

import common.UUID
import common.uuid
import nl.avwie.vdom.MVU
import kotlin.test.Test

class KanvasTests {

    fun initialState(uuid1: UUID, uuid2: UUID): Model = Model(
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
                entityId = uuid1,
                position = Position.Grid(0, 0),
            ),
            Item(
                entityId = uuid2,
                position = Position.Grid(2, 2),
            )
        )
    )

    @Test
    fun bug() {
        val target = TestRendererTarget()
        val uuid1 = uuid()
        val uuid2 = uuid()
        val mvu = MVU(
            target = target,
            initialState = initialState(uuid1, uuid2),
            render = ::render,
            update = ::update
        )

        val message = MouseClick(uuid2)
        mvu.dispatch(message)
        mvu.dispatch(message)
    }
}
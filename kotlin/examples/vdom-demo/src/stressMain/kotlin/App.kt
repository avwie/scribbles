package nl.avwie.dom

import kotlinx.browser.document
import kotlinx.browser.window
import nl.avwie.vdom.*
import org.w3c.dom.Element
import kotlin.math.abs
import kotlin.random.Random

data class Dynamics(val x: Double, val y: Double, val dx: Double, val dy: Double)
data class Color(val red: Int, val green: Int, val blue: Int) {
    private fun Int.hex(): String = this.toString(16).padStart(2, '0')
    fun hex(): String = "#${red.hex()}${green.hex()}${blue.hex()}"
}
data class Ball(val dynamics: Dynamics, val radius: Double, val color: Color)
data class Area(val width: Double, val height: Double)
data class State(val area: Area, val balls: List<Ball>)

fun initState(noOfBalls: Int, width: Double, height: Double): State = State(
    area = Area(width, height),
    balls = (0 until noOfBalls).map {
        Ball(
            dynamics = Dynamics(
                x = Random.nextDouble(width),
                y = Random.nextDouble(height),
                dx = Random.nextDouble(-1.0, 1.0) * width / 5000.0,
                dy = Random.nextDouble(-1.0, 1.0) * height / 5000.0
            ),
            radius = Random.nextDouble(minOf(width, height) / 20),
            color = Color(
                red = Random.nextInt(256),
                green = Random.nextInt(256),
                blue = Random.nextInt(256)
            )
        )
    }
)

fun renderState(state: State) = svg<String> {
    "width" by state.area.width.toString()
    "height" by state.area.height.toString()

    state.balls.forEachIndexed { i, ball ->
        "circle" {
            "cx" by ball.dynamics.x.toString()
            "cy" by ball.dynamics.y.toString()
            "r" by ball.radius.toString()
            "fill" by ball.color.hex()

            event("click", "clicked $i!")
        }
    }
}

fun walls(u: Double, du: Double, limit: Double): Pair<Double, Double> = when {
    u < 0 -> abs(u) to -du
    u > limit -> limit - (u - limit) to -du
    else -> u to du
}

fun updateState(state: State, dt: Double): State = state.copy(
    balls = state.balls.map { ball ->
        val (x0, y0, dx0, dy0) = ball.dynamics
        val (x1, dx1) = walls(x0 + dx0 * dt, dx0, state.area.width)
        val (y1, dy1) = walls(y0 + dy0 * dt, dy0, state.area.height)
        ball.copy(
            dynamics = Dynamics(x1, y1, dx1, dy1)
        )
    }
)

data class Update<S, Msg>(
    val state: S,
    val render: (S) -> Node<Msg>,
    val update: (S) -> S,
    val renderer: Renderer<Msg, Element>,
) {
    fun next(): Update<S, Msg> {
        val newState = update(state)
        val newVDom = render(newState)
        renderer.render(newVDom)
        return copy(
            state = newState,
        )
    }

    companion object {
        fun <S> build(mount: Element, initialState: S, render: (S) -> Node<String>, update: (S) -> S): Update<S, String> {
            val vDom = render(initialState)
            val target = BrowserDocumentTarget(mount)
            val renderer = Renderer(target, Dispatcher.print<String>())
            renderer.render(vDom)
            return Update(initialState, render, update, renderer)
        }
    }
}

fun main() {
    val container = document.getElementById("app")!!
    val dt = 1000 / 60
    var updater = Update.build(
        mount = container,
        initialState = initState(100, 1280.0, 1024.0),
        render = { state -> renderState(state) },
        update = { state -> updateState(state, dt.toDouble()) }
    )

    window.setInterval({ updater = updater.next() }, dt)
}
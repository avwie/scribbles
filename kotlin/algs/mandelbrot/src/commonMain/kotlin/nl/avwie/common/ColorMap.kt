package nl.avwie.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

class ColorMap(
    private val steps : List<Pair<Float, Color>>
) {
    constructor(vararg steps: Pair<Float, Color>): this(steps.toList())

    init {
        require(steps.first().first == 0.0f)
        require(steps.map { it.first }.windowed(2).all { (a, b) -> a < b })
        require(steps.last().first == 1.0f)
    }

    operator fun get(value: Float): Color {
        val boundedValue = value.coerceIn(0.0f, 1.0f)
        val (start, stop) = steps.windowed(2).first { (start, stop) -> start.first <= value && stop.first >= value  }
        val fraction = (boundedValue - start.first) / (stop.first - start.first)
        return lerp(start.second, stop.second, fraction)
    }

    companion object {
        val Plasma = ColorMap(
            0.00f to Color(13, 8, 135),
            0.14f to Color(84, 2, 163),
            0.29f to Color(139, 10, 165),
            0.43f to Color(185, 50, 137),
            0.57f to Color(219, 92, 104),
            0.71f to Color(244, 136, 73),
            0.86f to Color(254, 188, 43),
            1.00f to Color(240, 249, 33)
        )
    }
}
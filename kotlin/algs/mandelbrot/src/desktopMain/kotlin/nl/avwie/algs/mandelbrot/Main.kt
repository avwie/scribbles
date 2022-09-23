package nl.avwie.algs.mandelbrot

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import nl.avwie.common.ColorMap

fun main() = singleWindowApplication(
    title = "Mandelbrot",
    state = WindowState(size = DpSize(640.dp, 480.dp))
) {
    MandelbrotViewer(
        limit = 512,
        x = -1.2,
        y = -0.31,
        xScale = 0.1,
        colorMap = ColorMap.Plasma
    )
}
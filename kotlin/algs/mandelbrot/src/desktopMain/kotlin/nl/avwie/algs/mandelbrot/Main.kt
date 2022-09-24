package nl.avwie.algs.mandelbrot

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import nl.avwie.common.ColorMap

fun main() = singleWindowApplication(
    title = "Mandelbrot",
    state = WindowState(size = DpSize(800.dp, 600.dp))
) {
    var x by remember { mutableStateOf(-1.141) }
    var y by remember { mutableStateOf(-0.2678) }
    var scale by remember { mutableStateOf(0.1) }

    MandelbrotViewer(
        x = x,
        y = y,
        xScale = scale,
        colorMap = ColorMap.Plasma,
        onClick = { x1, y1 ->
            x = x1
            y = y1
        },
        onZoomIn = {
            scale /= 2
       },
        onZoomOut = { scale *= 2 }
    )
}
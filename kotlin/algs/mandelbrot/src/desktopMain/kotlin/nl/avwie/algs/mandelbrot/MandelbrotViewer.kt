package nl.avwie.algs.mandelbrot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import nl.avwie.common.ColorMap
import toImageBitmap

@Composable fun MandelbrotViewer(limit: Int, x: Double, y: Double, xScale: Double, colorMap: ColorMap)  {
    val colors by derivedStateOf { (0 .. limit).map { colorMap[it.toFloat() / limit] }.toTypedArray() }

    Column {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val options by derivedStateOf {
                Mandelbrot.ImageOptions.fromViewport(
                    canvasWidth.toInt(),
                    canvasHeight.toInt(),
                    x,
                    y,
                    xScale,
                    limit
                )
            }

            val result by derivedStateOf { Mandelbrot.render(options) }
            result.items().forEach { (rowCol, value) ->
                val (row, col) = rowCol
                val color = when (value) {
                    limit -> Color.Black
                    else -> colors[value]
                }

                drawPoints(
                    points = listOf(Offset(col.toFloat(), row.toFloat())),
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = 1.0f
                )
            }

            //drawImage(result.toImageBitmap(canvasWidth.toInt(), canvasHeight.toInt(), colorMap, limit))
        }
    }
}
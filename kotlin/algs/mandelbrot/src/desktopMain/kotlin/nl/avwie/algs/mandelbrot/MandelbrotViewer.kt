package nl.avwie.algs.mandelbrot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntSize
import nl.avwie.common.ColorMap

@OptIn(ExperimentalComposeUiApi::class)
@Composable fun MandelbrotViewer(
    limit: Int,
    x: Double,
    y: Double,
    xScale: Double,
    colorMap: ColorMap,
    onClick: (x: Double, y: Double) -> Unit = { _, _ -> },
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {}
)  {
    Column {
        var canvasWidth by remember { mutableStateOf(0) }
        var canvasHeight by remember { mutableStateOf(0) }
        val options by derivedStateOf {
            MandelbrotMap.Options.fromViewport(
                canvasWidth,
                canvasHeight,
                x,
                y,
                xScale,
                limit
            )
        }

        val requester = remember { FocusRequester() }
        Canvas(modifier = Modifier
            .fillMaxSize()
            .focusRequester(requester)
            .focusable()
            .onPointerEvent(PointerEventType.Release) {
                val position = it.changes.first().position
                val (x1, y1) = options.convertScreenCoordinates(position.x / density, position.y / density)
                onClick(x1, y1)
            }
            .onKeyEvent {
                when (it.key) {
                    Key.DirectionUp -> { onZoomIn(); true}
                    Key.DirectionDown -> { onZoomOut(); true}
                    else -> false
                }
            }
        ) {
            canvasWidth = (size.width / density).toInt()
            canvasHeight = (size.height / density).toInt()

            val result by derivedStateOf { MandelbrotMap(options) }
            drawImage(
                image = result.asBitmap(colorMap).asComposeImageBitmap(),
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )
        }

        LaunchedEffect(Unit) {
            requester.requestFocus()
        }
    }
}
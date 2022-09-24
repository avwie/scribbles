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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntSize
import nl.avwie.common.ColorMap
import org.jetbrains.skia.Bitmap

@OptIn(ExperimentalComposeUiApi::class)
@Composable fun MandelbrotViewer(
    x: Double,
    y: Double,
    xScale: Double,
    colorMap: ColorMap,
    onClick: (x: Double, y: Double) -> Unit = { _, _ -> },
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {}
)  {
    var canvasWidth by remember { mutableStateOf(1) }
    var canvasHeight by remember { mutableStateOf(1) }

    val options =  MandelbrotMap.Options.fromViewport(
        canvasWidth,
        canvasHeight,
        x,
        y,
        xScale,
        2048
    )

    var currentResolution by remember(options) { mutableStateOf(64) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val requester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        requester.requestFocus()
    }

    LaunchedEffect(currentResolution, options) {
        bitmap = MandelbrotMap.parallel(options.withResolution(currentResolution)).asBitmap(colorMap)
        if (currentResolution > 1) {
            currentResolution /= 4
        }
    }

    Column {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .focusRequester(requester)
            .focusable()
            .onPointerEvent(PointerEventType.Release) {
                val position = it.changes.first().position
                val (x1, y1) = options.convertScreenCoordinates(position.x, position.y)
                onClick(x1, y1)
            }
            .onKeyEvent {

                when {
                    it.type != KeyEventType.KeyUp -> false
                    it.key == Key.DirectionUp -> { onZoomIn(); true}
                    it.key == Key.DirectionDown -> { onZoomOut(); true}
                    else -> false
                }
            }
        ) {
            canvasWidth = size.width.toInt()
            canvasHeight = size.height.toInt()
            if (bitmap != null) {
                drawImage(
                    image = bitmap!!.asComposeImageBitmap(),
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                )
            }
        }
    }
}
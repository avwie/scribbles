import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toArgb
import nl.avwie.algs.mandelbrot.Array2D
import nl.avwie.common.ColorMap
import org.jetbrains.skia.*
import kotlin.system.measureTimeMillis

fun Array2D<Int>.toImageBitmap(width: Int, height: Int, colorMap: ColorMap, limit: Int): ImageBitmap {
    val bitmap = Bitmap()
    val info = ImageInfo(
        colorInfo = ColorInfo(
            colorType = ColorType.RGBA_8888,
            alphaType = ColorAlphaType.PREMUL,
            colorSpace = ColorSpace.sRGB
        ),
        width = width,
        height = height
    )
    bitmap.allocPixels(info)

    val colors = (0 .. limit).map { colorMap[it.toFloat() / limit] }.toTypedArray()
    val bytes = ByteArray(width * height * ColorType.RGBA_8888.bytesPerPixel)
    this.buffer.forEachIndexed { i, v ->
        val c = when (v) {
            null -> Color.Black
            limit -> Color.Black
            else -> colors[v]
        }
        bytes[i * ColorType.RGBA_8888.bytesPerPixel + 0] = (c.red * 255).toInt().toByte()
        bytes[i * ColorType.RGBA_8888.bytesPerPixel + 1] = (c.green * 255).toInt().toByte()
        bytes[i * ColorType.RGBA_8888.bytesPerPixel + 2] = (c.blue * 255).toInt().toByte()
        bytes[i * ColorType.RGBA_8888.bytesPerPixel + 3] = (255).toByte()
    }

    bitmap.installPixels(bytes)
    return bitmap.asComposeImageBitmap()
}
package nl.avwie.algs.mandelbrot

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*
import nl.avwie.common.ColorMap
import org.jetbrains.skia.*

fun mandelbrot(cx0: Double, cy0: Double, limit: Int = 1000): Int {
    var i = 0;
    var (xn, yn) = 0.0 to 0.0

    var xtemp: Double
    var ytemp: Double;
    while (xn * xn + yn * yn < 4 && i < limit) {
        xtemp = xn * xn - yn * yn
        ytemp = 2 * xn * yn
        xn = xtemp + cx0
        yn = ytemp + cy0
        i++
    }
    return i;
}

class MandelbrotMap(
    val options: Options,
    private val buffer: IntArray
) {
    val width: Int = options.xRes
    val height: Int = options.yRes

    operator fun get(x: Int, y: Int): Int {
        require(x in 0 until width)
        require(y in 0 until height)
        return buffer[y * width + x]
    }

    fun asBitmap(colorMap: ColorMap): Bitmap {
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

        val min = buffer.minOrNull() ?: 0
        val max = buffer.maxOrNull()?.plus(1) ?: options.limit
        val colors = (0 .. (max - min)).map { colorMap[it.toFloat() / (max - min)] }.toTypedArray()

        val bytes = ByteArray(width * height * ColorType.RGBA_8888.bytesPerPixel)
        this.buffer.forEachIndexed { index, value ->
            val c = when (value) {
                options.limit -> Color.Black
                else -> colors[value - min]
            }
            bytes[index * ColorType.RGBA_8888.bytesPerPixel + 0] = (c.red * 255).toInt().toByte()
            bytes[index * ColorType.RGBA_8888.bytesPerPixel + 1] = (c.green * 255).toInt().toByte()
            bytes[index * ColorType.RGBA_8888.bytesPerPixel + 2] = (c.blue * 255).toInt().toByte()
            bytes[index * ColorType.RGBA_8888.bytesPerPixel + 3] = (255).toByte()
        }
        bitmap.installPixels(bytes)
        return bitmap
    }

    companion object {
        operator fun invoke(options: Options): MandelbrotMap {
            val buffer = IntArray(options.xRes * options.yRes) { 0 }
            var cx: Double
            var cy = options.yMin
            var x: Int
            var y = 0
            while (cy <= options.yMax) {
                x = 0
                cx = options.xMin
                while (cx <= options.xMax) {
                    buffer[y * options.xRes + x] = mandelbrot(cx, cy, options.limit)
                    cx += options.deltaX
                    x += 1
                }
                cy += options.deltaY
                y += 1
            }
            return MandelbrotMap(options, buffer)
        }

        suspend fun parallel(options: Options, resolution: Int = 1): MandelbrotMap {
            val buffer = IntArray(options.xRes / resolution * options.yRes / resolution) { 0 }
            withContext(Dispatchers.Default) {
                (0 until options.yRes / resolution).forEach { y ->
                    launch {
                        (0 until options.xRes / resolution).forEach { x ->
                            val cx0 = options.xMin + x * options.deltaX
                            val cy0 = options.yMin + y * options.deltaY
                            buffer[y * options.xRes + x] = mandelbrot(cx0, cy0, options.limit)
                        }
                    }
                }
            }
            return MandelbrotMap(options, buffer)
        }
    }

    data class Options(
        val xMin: Double,
        val xMax: Double,
        val xRes: Int,
        val yMin: Double,
        val yMax: Double,
        val yRes: Int,
        val limit: Int
    ) {
        val deltaX = (xMax - xMin) / (xRes - 1)
        val deltaY = (yMax - yMin) / (yRes - 1)

        fun convertScreenCoordinates(x: Float, y: Float): Pair<Double, Double> {
            return Pair(
                xMin + (xMax - xMin) * x / xRes,
                yMin + (yMax - yMin) * y / yRes
            )
        }

        fun withResolution(resolution: Int): Options {
            require(resolution >= 1)
            return copy(
                xRes = xRes / resolution,
                yRes = yRes / resolution,
            )
        }

        companion object {
            fun default(xRes: Int, yRes: Int, limit: Int) = Options(
                xMin = -2.00,
                xMax = 0.47,
                yMin = -1.12,
                yMax = 1.12,
                xRes = xRes,
                yRes = yRes,
                limit = limit
            )

            fun fromViewport(width: Int, height: Int, x: Double, y: Double, xScale: Double = 4.0, limit: Int) = Options(
                xMin = x - 0.5 * xScale,
                xMax = x + 0.5 * xScale,
                yMin = y - 0.5 * xScale * height / width,
                yMax = y + 0.5 * xScale * height / width,
                xRes = width,
                yRes = height,
                limit = limit
            )
        }
    }
}
package nl.avwie.algs.mandelbrot

object Mandelbrot {
    fun calculateIterations(cx0: Double, cy0: Double, limit: Int = 1000): Int {
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

    data class ImageOptions(
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

        companion object {
            fun default(xRes: Int, yRes: Int, limit: Int) = ImageOptions(
                xMin = -2.00,
                xMax = 0.47,
                yMin = -1.12,
                yMax = 1.12,
                xRes = xRes,
                yRes = yRes,
                limit = limit
            )

            fun fromViewport(width: Int, height: Int, x: Double, y: Double, xScale: Double = 4.0, limit: Int) = ImageOptions(
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

    fun render(options: ImageOptions): Array2D<Int> {
        val result = Array2D<Int>(options.yRes, options.xRes)
        var cx: Double
        var cy = options.yMin
        var r = 0
        var c: Int
        while (cy <= options.yMax) {
            c = 0
            cx = options.xMin
            while (cx <= options.xMax) {
                result[r, c] = calculateIterations(cx, cy, options.limit)
                cx += options.deltaX
                c += 1
            }
            cy += options.deltaY
            r += 1
        }
        return result
    }
}
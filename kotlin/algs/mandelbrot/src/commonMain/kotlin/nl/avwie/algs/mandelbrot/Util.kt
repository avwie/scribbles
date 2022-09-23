package nl.avwie.algs.mandelbrot


class Array2D<T>(val rows: Int, val cols: Int, val buffer: Array<T?>) {

    operator fun get(row: Int, col: Int): T? = buffer[row * cols + col]
    operator fun set(row: Int, col: Int, value: T) {
        buffer[row * cols + col] = value
    }

    fun items(): Sequence<Pair<Pair<Int, Int>, T>> = (0 until rows).asSequence()
        .flatMap { row ->
            (0 until cols).asSequence().mapNotNull { col ->
                get(row, col)?.let { Pair(row to col, it) }
            }
        }

    companion object {
        inline operator fun <reified T> invoke(rows: Int, cols: Int): Array2D<T> {
            return Array2D(rows, cols, arrayOfNulls(rows * cols))
        }
    }
}
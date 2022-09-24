package nl.avwie.algs.mandelbrot

import kotlin.test.Test
import kotlin.test.assertEquals

class MandelbrotTests {

    @Test
    fun calculateIterations() {
        val limit = 1000
        assertEquals(mandelbrot(1.0, 0.0, limit), 2)
        assertEquals(mandelbrot(-1.0, 0.0, limit), limit)
        assertEquals(mandelbrot(-1.0, -1.0, limit), 3)
        assertEquals(mandelbrot(-1.0, -0.5, limit), 5)
        assertEquals(mandelbrot(-1.0, 0.5, limit), 5)
    }
}
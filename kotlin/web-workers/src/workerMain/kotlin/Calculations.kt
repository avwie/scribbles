import kotlin.random.Random

fun approximatePI(iterations: Int): Double {
    var inner = 0
    var px: Double
    var py: Double
    repeat(iterations) {
        px = Random.nextDouble(-1.0, 1.0)
        py = Random.nextDouble(-1.0, 1.0)
        if (px * px + py * py <= 1) inner++
    }
    return 4 * inner.toDouble() / iterations
}
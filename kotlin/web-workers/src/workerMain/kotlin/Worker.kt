import nl.avwie.webworkers.*
import kotlin.random.Random

var workerId : String = "Worker"

fun main() = worker { request ->
    when (request) {
        is Initialize -> {
            workerId = request.workerId
            Initialized
        }
        is PIApproximation -> PIApproximationResult(approximatePI(request.iterations))
    }
}

fun approximatePI(iterations: Int): Double {
    var inner: Int = 0
    var px = 0.0
    var py = 0.0
    repeat(iterations) {
        px = Random.nextDouble(-1.0, 1.0)
        py = Random.nextDouble(-1.0, 1.0)
        if (px * px + py * py <= 1) inner++
    }
    return 4 * inner.toDouble() / iterations
}
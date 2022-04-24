import nl.avwie.webworkers.*
import kotlin.random.Random

fun main() = WorkerScope { request ->
    when (request) {
        is PIApproximation -> {
            PIApproximationResult(approximatePI(request.iterations)).let {
                if (Random.nextBoolean()) it else throw Error("Random failure!!!")
            }
        }
    }
}
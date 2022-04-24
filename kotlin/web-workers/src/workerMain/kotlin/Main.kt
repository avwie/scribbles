import nl.avwie.webworkers.*
import kotlin.random.Random

/*fun main() = WorkerScope.handleRequest { request ->
    when (request) {
        is PIApproximation -> {
            PIApproximationResult(approximatePI(request.iterations)).let {
                if (Random.nextBoolean()) it else throw Error("Random failure!!!")
            }
        }
    }
}*/

fun main() = worker {
    receive { data -> "Sending back from $workerId: $data" }
}
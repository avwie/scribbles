import kotlinx.coroutines.delay
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

suspend fun main() = worker { data, workerId ->
    delay(1000)
    "Sending back from $workerId: $data"
}
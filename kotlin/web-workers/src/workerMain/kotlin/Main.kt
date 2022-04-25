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

/*fun main() = worker {
    receive { data ->
        delay(1000)
        "Sending back from $workerId: $data"
    }
}*/

fun main() = worker {
    receiveRequest { request ->
        when (request) {
            is PIApproximation -> PIApproximationResult(approximatePI(request.iterations))
            is Sleep -> {
                delay(request.ms)
                SleepResult(request.ms)
            }
        }
    }
}
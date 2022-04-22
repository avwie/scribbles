import nl.avwie.webworkers.PIApproximation

fun main() {
    val pool = WorkerPool(10, "./worker.js")
    with(pool) {
        repeat(20) { no ->
            request(PIApproximation(10000000)) { result ->
                console.log(no + 1, "Approximation result: ", result.pi)
            }
        }
    }
}
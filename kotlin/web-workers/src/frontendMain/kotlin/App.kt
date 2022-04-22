import kotlinx.browser.document
import nl.avwie.webworkers.PIApproximation

fun main() {
    val output = document.getElementById("output")!!
    val pool = WorkerPool(10, "./worker.js")
    with(pool) {
        repeat(20) { no ->
            request(PIApproximation(10000000)) { worker, result ->
                output.textContent += "$worker: ${result.pi}\n"
            }
        }
    }
}
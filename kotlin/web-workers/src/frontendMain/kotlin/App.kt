import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.avwie.webworkers.PIApproximation
import org.w3c.dom.url.URLSearchParams

fun main() {
    val output = document.getElementById("output")!!
    val pool = WorkerPool(10, "./worker.js")
    with(pool) {
        repeat(20) { i ->
            GlobalScope.launch {
                try {
                    val pi = request(PIApproximation(10000000)).pi
                    output.textContent += "$i:\t:$pi\n"
                } catch (e: WorkerException) {
                    output.textContent += "$i:\tFailed: ${e.message}\n"
                }
            }
        }
    }
}
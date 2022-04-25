import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.avwie.webworkers.PIApproximation
import nl.avwie.webworkers.Sleep
import org.w3c.dom.Worker
import kotlin.random.Random

/*fun main() {
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
}*/

/*fun main() {
    GlobalScope.launch {
        val worker = Worker("./worker.js?id=ping-server")
        console.log(worker.send("PING!").data.toString())
    }
}*/

/*fun main() {
    val pool = WorkerPool(10, "./worker.js")
    repeat(50) { i ->
        GlobalScope.launch {
            console.log(pool.send("Ping $i"))
        }
    }
}*/

fun main() {
    val pool = WorkerPool(10, "./worker.js")
    repeat(20) { i ->
        GlobalScope.launch {
            when {
                i % 2 == 0 -> console.log("PI approximation: ", pool.request(PIApproximation(10000000)).pi)
                else -> console.log("Sleeping for: ", pool.request(Sleep(Random.nextLong(500, 5000))).ms.toString())
            }
        }
    }
}
import nl.avwie.webworkers.Ping
import nl.avwie.webworkers.Sarcasm
import org.w3c.dom.Worker

fun main() {
    val worker = Worker("./worker.js");
    with(worker) {
        request(Ping) { response ->
            console.log(response)
        }

        request(Sarcasm("Hallooooo!")) { response ->
            console.log(response)
        }
    }
}
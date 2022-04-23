import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.webworkers.*
import org.w3c.dom.DedicatedWorkerGlobalScope
import kotlin.random.Random

var workerId : String = "Worker"
val isWorkerGlobalScope = js("typeof(WorkerGlobalScope) !== \"undefined\"") as? Boolean  ?: throw IllegalStateException("Boolean cast went wrong")

fun main() = worker { request ->
    when (request) {
        is Initialize -> {
            workerId = request.workerId
            Initialized
        }
        is PIApproximation -> {
            if (Random.nextBoolean()) PIApproximationResult(approximatePI(request.iterations)) else throw Error("Random failure!!!")
        }
    }
}

fun worker(block: (request: Request<*>) -> RequestResult) {
    if (isWorkerGlobalScope) {
        val self = js("self") as? DedicatedWorkerGlobalScope ?: throw IllegalStateException("DedicatedWorkerGlobalScope cast went wrong")
        self.onmessage = { messageEvent ->
            console.log("$workerId received", messageEvent.data.toString())
            val response = try {
                val message = Json.decodeFromString<Message>(messageEvent.data.toString())
                val result = block(message as Request<*>)
                Response(result = result, error = null)
            } catch (e: Throwable) {
                Response(result = null, error = e.message)
            }
            val responseJson = Json.encodeToString(response)
            console.log("$workerId responds", responseJson)
            self.postMessage(responseJson)
        }
    }
}

fun approximatePI(iterations: Int): Double {
    var inner: Int = 0
    var px: Double
    var py: Double
    repeat(iterations) {
        px = Random.nextDouble(-1.0, 1.0)
        py = Random.nextDouble(-1.0, 1.0)
        if (px * px + py * py <= 1) inner++
    }
    return 4 * inner.toDouble() / iterations
}
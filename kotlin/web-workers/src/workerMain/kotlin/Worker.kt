import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.webworkers.*
import org.w3c.dom.DedicatedWorkerGlobalScope
import org.w3c.dom.url.URLSearchParams
import kotlin.random.Random

fun main() = workerScope {
    handleRequest { request ->
        when (request) {
            is PIApproximation -> {
                PIApproximationResult(approximatePI(request.iterations)).let {
                    if (Random.nextBoolean()) it else throw Error("Random failure!!!")
                }
            }
        }
    }
}

val isWorkerGlobalScope = js("typeof(WorkerGlobalScope) !== \"undefined\"") as? Boolean  ?: throw IllegalStateException("Boolean cast went wrong")

fun workerScope(block: DedicatedWorkerGlobalScope.() -> Unit) {
    if (isWorkerGlobalScope) {
        val self = js("self") as? DedicatedWorkerGlobalScope ?: throw IllegalStateException("DedicatedWorkerGlobalScope cast went wrong")
        block(self)
    }
}

val DedicatedWorkerGlobalScope.workerId: String get() = URLSearchParams(location.search).get("id") ?: "Unknown worker"

fun DedicatedWorkerGlobalScope.handleRequest(block: (request: Request<*>) -> RequestResult) {
    onmessage = { messageEvent ->
        console.log("$workerId received", messageEvent.data.toString())
        val response = try {
            val message = Json.decodeFromString<Message>(messageEvent.data.toString())
            val result = block(message as Request<*>)
            Response(workerId = workerId, result = result, error = null)
        } catch (e: Throwable) {
            Response(workerId = workerId, result = null, error = e.message)
        }
        val responseJson = Json.encodeToString(response)
        console.log("$workerId responds", responseJson)
        postMessage(responseJson)
    }
}

fun approximatePI(iterations: Int): Double {
    var inner = 0
    var px: Double
    var py: Double
    repeat(iterations) {
        px = Random.nextDouble(-1.0, 1.0)
        py = Random.nextDouble(-1.0, 1.0)
        if (px * px + py * py <= 1) inner++
    }
    return 4 * inner.toDouble() / iterations
}
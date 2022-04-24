import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.webworkers.Message
import nl.avwie.webworkers.Request
import nl.avwie.webworkers.RequestResult
import nl.avwie.webworkers.Response
import org.w3c.dom.DedicatedWorkerGlobalScope
import org.w3c.dom.url.URLSearchParams

class WorkerScope(private val self: DedicatedWorkerGlobalScope) {
    private val workerId = URLSearchParams(self.location.search).get("id") ?: "Unknown worker"

    private fun handleRequest(block: (request: Request<*>) -> RequestResult) {
        self.onmessage = { messageEvent ->
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
            self.postMessage(responseJson)
        }
    }

    companion object {
        private val isWorkerGlobalScope = js("typeof(WorkerGlobalScope) !== \"undefined\"") as? Boolean  ?: throw IllegalStateException("Boolean cast went wrong")

        operator fun invoke(block: (request: Request<*>) -> RequestResult) {
            if (isWorkerGlobalScope) {
                val self = js("self") as? DedicatedWorkerGlobalScope ?: throw IllegalStateException("DedicatedWorkerGlobalScope cast went wrong")
                val workerScope = WorkerScope(self)
                workerScope.handleRequest(block)
            }
        }
    }
}
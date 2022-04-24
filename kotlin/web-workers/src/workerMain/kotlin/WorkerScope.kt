import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.webworkers.Message
import nl.avwie.webworkers.Request
import nl.avwie.webworkers.RequestResult
import nl.avwie.webworkers.Response
import org.w3c.dom.DedicatedWorkerGlobalScope
import org.w3c.dom.url.URLSearchParams

fun worker(block: WorkerScope.() -> Unit) {
    WorkerScope()?.let(block)
}

class WorkerScope(private val self: DedicatedWorkerGlobalScope) {
    val workerId = URLSearchParams(self.location.search).get("id") ?: "Unknown worker"

    fun receive(block: (String) -> String) {
        self.onmessage = { messageEvent ->
            val response = try {
                block(messageEvent.data.toString())
            } catch (e: Throwable) {
                e.message ?: "Something went wrong"
            }
            self.postMessage(response)
        }
    }

    fun receiveRequest(block: (request: Request<*>) -> RequestResult) = receive { data ->
        val message = Json.decodeFromString<Message>(data)
        val response = try {
            val result = block(message as Request<*>)
            Response(workerId = workerId, result = result, error = null)
        } catch (e: Throwable) {
            Response(workerId = workerId, result = null, error = e.message)
        }
        Json.encodeToString(response)
    }

    companion object {
        operator fun invoke(): WorkerScope? {
            val isWorkerGlobalScope = js("typeof(WorkerGlobalScope) !== \"undefined\"") as? Boolean  ?: throw IllegalStateException("Boolean cast went wrong")
            if (!isWorkerGlobalScope) return null

            val self = js("self") as? DedicatedWorkerGlobalScope
                ?: throw IllegalStateException("DedicatedWorkerGlobalScope cast went wrong")
            return WorkerScope(self)
        }
    }
}
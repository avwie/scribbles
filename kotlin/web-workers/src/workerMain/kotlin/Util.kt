import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.webworkers.*
import org.w3c.dom.DedicatedWorkerGlobalScope
import kotlin.reflect.KClass

private val isWorkerGlobalScope = js("typeof(WorkerGlobalScope) !== \"undefined\"") as? Boolean  ?: throw IllegalStateException("Boolean cast went wrong")

fun worker(block: (request: Request<*>) -> Message) {
    if (isWorkerGlobalScope) {
        val self = js("self") as? DedicatedWorkerGlobalScope ?: throw IllegalStateException("DedicatedWorkerGlobalScope cast went wrong")
        self.onmessage = { messageEvent ->
            console.log("[Worker] received", messageEvent.data.toString())
            val message = Json.decodeFromString<Message>(messageEvent.data.toString())
            val response = Json.encodeToString(block(message as Request<*>))
            console.log("[Worker] responding", response)
            self.postMessage(response)
        }
    }
}
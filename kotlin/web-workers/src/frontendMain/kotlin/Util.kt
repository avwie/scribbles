import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.webworkers.Request
import nl.avwie.webworkers.RequestResult
import nl.avwie.webworkers.Response
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WorkerException(message: String) : Throwable(message)

suspend fun Worker.send(data: String) = suspendCoroutine<MessageEvent> { continuation ->
    this.onmessage = { messageEvent ->
        continuation.resume(messageEvent)
    }
    this.onerror = { event -> continuation.resumeWithException(WorkerException(event.type))}
    this.postMessage(data)
}

@Suppress("UNCHECKED_CAST")
suspend fun <R : RequestResult> Worker.request(request: Request<R>): R {
    val data = Json.encodeToString(request as Request<RequestResult>)
    val messageEvent = send(data)
    val response = Json.decodeFromString<Response>(messageEvent.data.toString())

    if (response.error != null) throw WorkerException(response.error)
    return response.result as R
}
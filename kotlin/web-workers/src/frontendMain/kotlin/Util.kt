import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.webworkers.Message
import nl.avwie.webworkers.Request
import nl.avwie.webworkers.RequestResult
import nl.avwie.webworkers.Response
import org.w3c.dom.Worker

fun Worker.postMessage(message: Message, callback: (response: Message) -> Unit) {
    this.onmessage = { messageEvent ->
        val returnMessage = Json.decodeFromString<Response>(messageEvent.data.toString())
        callback(returnMessage)
    }
    this.postMessage(Json.encodeToString(message))
}

@Suppress("UNCHECKED_CAST")
fun <R : RequestResult> Worker.request(request: Request<R>, callback: (response: R) -> Unit) {
    this.postMessage(request) { message ->
        val response = message as Response
        response.result?.let { callback(it as R) } ?: throw Error(response.error)
    }
}
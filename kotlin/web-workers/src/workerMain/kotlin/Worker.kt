import nl.avwie.webworkers.Ping
import nl.avwie.webworkers.Pong
import nl.avwie.webworkers.Sarcasm
import nl.avwie.webworkers.StringResponse
import kotlin.random.Random

fun main() = worker { request ->
    when (request) {
        Ping -> Pong
        is Sarcasm -> StringResponse(request.payload.map { c -> if (Random.nextBoolean()) c else c.uppercase() }.joinToString())
    }
}
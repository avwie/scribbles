package nl.avwie.examples.crdt

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.forEach
import nl.avwie.common.uuid
import org.slf4j.LoggerFactory
import java.net.SocketException
import java.util.logging.Logger


fun main() {
    val stream = MutableSharedFlow<String>()
    val logger = LoggerFactory.getLogger("Main")

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CallLogging)
        install(CORS) {
            allowHost("*")
        }
        routing {
            get("/") {
                call.respondText("Welcome to the CRDT server")
            }

            post("/publish") {
                val received = call.receive<String>()
                stream.emit(received)
                call.respondText("Published: $received")
            }

            get("/subscribe") {
                logger.info("Subscriber subscribed!x")
                coroutineScope {
                    call.respondServerSentEvent(stream)
                }
            }
        }
    }.start(wait = true)
}

suspend fun ApplicationCall.respondServerSentEvent(events: SharedFlow<String>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondTextWriter(contentType = ContentType.Text.EventStream) {
        events.collect {
            write("id:${uuid()}\n")
            write("event:message\n")
            write("data:$it\n")
            write("\n")
            flush()
        }
    }
}
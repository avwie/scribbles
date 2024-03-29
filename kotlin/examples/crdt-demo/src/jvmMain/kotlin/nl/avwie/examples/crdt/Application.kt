package nl.avwie.examples.crdt

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
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
import java.io.File
import java.net.SocketException
import java.util.logging.Logger


fun main() {
    val stream = MutableSharedFlow<String>()
    embeddedServer(Netty, port = 8080, host = "localhost") {
        install(CallLogging)
        install(CORS) {
            allowHost("*")
        }
        routing {
            static("/") {
                staticRootFolder = File("./build/distributions")
                file("", "index.html")
                files(".")
            }

            post("/publish") {
                val received = call.receive<String>()
                stream.emit(received)
                call.respondText("Published: $received")
            }

            get("/subscribe") {
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                    stream.collect {
                        write("id:${uuid()}\n")
                        write("event:message\n")
                        write("data:$it\n")
                        write("\n")
                        flush()
                    }
                }
            }
        }
    }.start(wait = true)
}
package hu.bme.aut

import hu.bme.aut.domain.model.Job
import hu.bme.aut.domain.model.Request
import hu.bme.aut.domain.model.Response
import hu.bme.aut.serialization.InstantAsStringSerializer
import hu.bme.aut.serialization.UUIDAsStringSerializer
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.imageio.ImageIO

class Server(private val apiKeys: Set<String>, private val tokens: Map<String, String>) {
    private val queues = ConcurrentHashMap<String, ConcurrentLinkedQueue<Job>>()

    private val server = embeddedServer(Netty, port = 8089) {
        install(CallLogging)
        install(ContentNegotiation) { json(
            Json {
                serializersModule = SerializersModule {
                    contextual(UUID::class, UUIDAsStringSerializer)
                    contextual(Instant::class, InstantAsStringSerializer)
                }
            }
        ) }

        routing {
            post("/enqueue") {
                println("eq")
            }

            get("/dequeue") {
                println("dq")
            }
        }
    }

    fun start() {
        server.start(wait = true)
    }

    fun stop() {
        server.stop(gracePeriodMillis = 1000, timeoutMillis = 5000)
    }
}

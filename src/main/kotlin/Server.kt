package hu.bme.aut

import hu.bme.aut.adapter.InMemoryBikeRepository
import hu.bme.aut.adapter.InMemoryRentalRepository
import hu.bme.aut.adapter.InMemoryUserRepository
import hu.bme.aut.domain.model.Bike
import hu.bme.aut.domain.model.Rental
import hu.bme.aut.domain.model.User
import hu.bme.aut.domain.service.BikeRepository
import hu.bme.aut.domain.service.RentalRepository
import hu.bme.aut.domain.service.UserRepository
import hu.bme.aut.serialization.*
import hu.bme.aut.serialization.request.*
import hu.bme.aut.serialization.response.*
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
import java.time.Instant
import java.util.*

class Server(
    private val apiKeys: Set<String>,
    private val userRepository: UserRepository = InMemoryUserRepository(),
    private val rentalRepository: RentalRepository = InMemoryRentalRepository(),
    private val bikeRepository: BikeRepository = InMemoryBikeRepository()
) {
    init {
        bikeRepository.save(Bike("860001"))
        bikeRepository.save(Bike("860002"))
        bikeRepository.save(Bike("860003"))
        bikeRepository.save(Bike("860004"))
    }

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
            post("/register.json") {
                println("register called")
                val request = call.receive<RegisterRequest>()

                if (request.apiKey !in apiKeys) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                if (userRepository.findByPhoneNumber(request.phoneNumber) != null) {
                    call.respond(HttpStatusCode.Conflict, "User already exists")
                    return@post
                }

                val generatedPin = (100_000..999_999).random().toString()

                val generatedLoginKey = List(32) {
                    ('a'..'z') + ('A'..'Z') + ('0'..'9')
                }.flatten().shuffled().take(32).joinToString("")

                val user = User(
                    domain = request.domain,
                    phoneNumber = request.phoneNumber,
                    lang = request.language,
                    pin = generatedPin,
                    loginKey = generatedLoginKey
                )

                userRepository.save(user)

                val response = RegisterResponse(
                    serverTime = System.currentTimeMillis() / 1000,
                    user = user
                )

                call.respond(HttpStatusCode.Created, response)
            }

            post("/login.json") {
                println("login called")
                val request = call.receive<LoginRequest>()

                if (request.apiKey !in apiKeys) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                val user: User? = when {
                    request.loginKey != null -> userRepository.findByLoginKey(request.loginKey)
                    request.mobile != null && request.pin != null -> {
                        userRepository.findByPhoneNumber(request.mobile)?.takeIf { it.pin == request.pin }
                    }
                    else -> null
                }

                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    val response = LoginResponse(
                        serverTime = System.currentTimeMillis() / 1000,
                        user = user
                    )
                    call.respond(HttpStatusCode.OK, response)
                }
            }

            post("/rent.json") {
                println("rent called")
                val request = call.receive<RentRequest>()

                if (request.apiKey !in apiKeys) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                val user = userRepository.findByLoginKey(request.loginKey)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                val bike = bikeRepository.findByNumber(request.bike)
                if (bike == null) {
                    call.respond(HttpStatusCode.NotFound, "Bike not found"); return@post
                }

                val ongoing = rentalRepository
                    .findByBike(bike.bikeNumber)
                    .firstOrNull { it.endTime == 0L }

                if (ongoing != null &&
                    ongoing.isOnBreak &&
                    ongoing.userKey == request.loginKey) {

                    val resumed = ongoing.copy(
                        isOnBreak        = false,
                        framelockLocked  = false
                    )
                    rentalRepository.save(resumed)

                    bikeRepository.save(bike.copy(available = false))

                    call.respond(
                        HttpStatusCode.Created,
                        RentResponse(
                            rental      = resumed,
                            serverTime  = System.currentTimeMillis() / 1000
                        )
                    )
                    return@post
                }

                if (!bike.available) {
                    call.respond(HttpStatusCode.Conflict, "Bike not available"); return@post
                }

                val nowSecs = System.currentTimeMillis() / 1000

                val newRental = Rental(
                    bike            = bike.bikeNumber,
                    userKey         = request.loginKey,
                    startPlaceLat   = 0.0,
                    startPlaceLng   = 0.0,
                    endPlaceLat     = 0.0,
                    endPlaceLng     = 0.0,
                    startTime       = nowSecs,
                    endTime         = 0,
                    isOnBreak       = false,
                    framelockLocked = false
                ).let { rentalRepository.save(it) }

                bikeRepository.save(bike.copy(available = false))

                call.respond(
                    HttpStatusCode.Created,
                    RentResponse(
                        rental      = newRental,
                        serverTime  = nowSecs
                    )
                )
            }

            post("/getRentalDetails.json") {
                println("getRentalDetails called")
                val request = call.receive<GetRentalDetailsRequest>()

                if (request.apiKey !in apiKeys) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                val user = userRepository.findByLoginKey(request.loginKey)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                val rental = rentalRepository.findById(request.rental)
                if (rental == null) {
                    call.respond(HttpStatusCode.NotFound, "Rental not found"); return@post
                }

                val response = GetRentalDetailsResponse(
                    rental = rental,
                    serverTime = System.currentTimeMillis() / 1000
                )
                call.respond(HttpStatusCode.OK, response)
            }

            post("/rentalBreak.json") {
                println("rentalBreak called")
                val request = call.receive<RentalBreakRequest>()

                if (request.apiKey !in apiKeys) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                val user = userRepository.findByLoginKey(request.loginKey)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                val rental = rentalRepository.findById(request.rental)
                if (rental == null) {
                    call.respond(HttpStatusCode.NotFound, "Rental not found"); return@post
                }

                val updated = rental.copy(isOnBreak = true)
                rentalRepository.save(updated)

                val response = RentalBreakResponse(
                    rental     = updated,
                    serverTime = System.currentTimeMillis() / 1000
                )
                call.respond(HttpStatusCode.OK, response)
            }

            post("/bikeState.json") {
                println("bikeState called")
                val request = call.receive<BikeStateRequest>()

                if (request.apiKey !in apiKeys) {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }

                var bike = bikeRepository.findByNumber(request.bike)
                    ?: Bike(bikeNumber = request.bike).also { bikeRepository.save(it) }

                if (request.locked) {
                    val ongoing = rentalRepository
                        .findByBike(bike.bikeNumber)
                        .firstOrNull { it.endTime == 0L }

                    if (ongoing != null) {
                        if (ongoing.isOnBreak) {
                            bike = bike.copy(available = false)
                            bikeRepository.save(bike)

                        } else {
                            val finished = ongoing.copy(
                                endPlaceLat      = request.lat,
                                endPlaceLng      = request.lng,
                                endTime          = System.currentTimeMillis() / 1000,
                                framelockLocked  = true
                            )
                            rentalRepository.save(finished)

                            bike = bike.copy(available = true)
                            bikeRepository.save(bike)
                        }
                    } else {
                        bike = bike.copy(available = true)
                        bikeRepository.save(bike)
                    }
                }

                call.respond(
                    HttpStatusCode.OK,
                    BikeStateResponse(
                        bike       = bike,
                        serverTime = System.currentTimeMillis() / 1000
                    )
                )
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

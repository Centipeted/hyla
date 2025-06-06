package hu.bme.aut.serialization.response

import hu.bme.aut.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val serverTime: Long,
    val user: User
)
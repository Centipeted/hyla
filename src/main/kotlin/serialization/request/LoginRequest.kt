package hu.bme.aut.serialization.request

import hu.bme.aut.domain.model.Request
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    override val apiKey: String,
    override val domain: String,
    override val language: String,
    val mobile: String?,
    val pin: String?,
    val loginKey: String?
) : Request
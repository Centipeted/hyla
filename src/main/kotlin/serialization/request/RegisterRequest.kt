package hu.bme.aut.serialization.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import hu.bme.aut.domain.model.Request

@Serializable
data class RegisterRequest(
    override val apiKey: String,
    override val domain: String,
    override val language: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("login_key") val loginKey: String? = null
) : Request
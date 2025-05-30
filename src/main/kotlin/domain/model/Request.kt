package hu.bme.aut.domain.model
import kotlinx.serialization.Serializable

interface Request{
    val apiKey: String
    val domain: String
    val language: String
}

@Serializable
data class LoginRequest(
    override val apiKey: String,
    override val domain: String,
    override val language: String,
    val mobile: String?,
    val pin: String?,
    val loginKey: String?
) : Request

data class KeyLoginRequest(
    override val apiKey: String,
    override val domain: String,
    override val language: String,

) : Request
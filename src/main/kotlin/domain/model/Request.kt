package hu.bme.aut.domain.model
import kotlinx.serialization.Serializable

interface Request{
    val apiKey: String
    val domain: String
    val language: String
}


package hu.bme.aut.serialization.request

import hu.bme.aut.domain.model.Request
import kotlinx.serialization.Serializable

@Serializable
data class RentRequest(
    override val apiKey: String,
    override val domain: String,
    override val language: String,
    val loginKey: String,
    val bike: String
) : Request
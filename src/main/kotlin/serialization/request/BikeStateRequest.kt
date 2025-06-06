package hu.bme.aut.serialization.request

import kotlinx.serialization.Serializable

@Serializable
data class BikeStateRequest(
    val apiKey: String,
    val bike: String,
    val locked: Boolean,
    val lat: Double,
    val lng: Double
)
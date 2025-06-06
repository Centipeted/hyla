package hu.bme.aut.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Bike(
    val bikeNumber: String,
    val available: Boolean = true
)
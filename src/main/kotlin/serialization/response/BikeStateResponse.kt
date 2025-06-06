package hu.bme.aut.serialization.response

import hu.bme.aut.domain.model.Bike
import kotlinx.serialization.Serializable

@Serializable
data class BikeStateResponse(
    val bike: Bike,
    val serverTime: Long
)
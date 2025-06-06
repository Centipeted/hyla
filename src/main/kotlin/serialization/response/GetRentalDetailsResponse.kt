package hu.bme.aut.serialization.response

import hu.bme.aut.domain.model.Rental
import kotlinx.serialization.Serializable

@Serializable
data class GetRentalDetailsResponse(
    val rental: Rental,
    val serverTime: Long
)
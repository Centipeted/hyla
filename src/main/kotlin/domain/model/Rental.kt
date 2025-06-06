package hu.bme.aut.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rental(
    val id: Long = 0,
    val bike: String,
    val userKey: String,

    @SerialName("start_place_lat") val startPlaceLat: Double,
    @SerialName("start_place_lng") val startPlaceLng: Double,
    @SerialName("end_place_lat") val endPlaceLat: Double,
    @SerialName("end_place_lng") val endPlaceLng: Double,
    @SerialName("start_time") val startTime: Long,
    @SerialName("end_time") val endTime: Long,
    @SerialName("break") val isOnBreak: Boolean,
    @SerialName("framelock_locked") val framelockLocked: Boolean
)
package hu.bme.aut.domain.model

import hu.bme.aut.serialization.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Response(
    @Serializable(with = UUIDAsStringSerializer::class)
    val id: UUID
)

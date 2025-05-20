package hu.bme.aut.domain.model

import hu.bme.aut.serialization.InstantAsStringSerializer
import hu.bme.aut.serialization.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class Job(
    @Serializable(with = UUIDAsStringSerializer::class)
    val id: UUID,
    val zpl: String,
    @Serializable(with = InstantAsStringSerializer::class)
    val time: Instant
)

package hu.bme.aut.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val key: String,
    val printerName: String,
    val zpl: String
)

package hu.bme.aut.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val domain: String,
    val lang: String,
    val phoneNumber: String,
    val pin: String? = null,
    val loginKey: String? = null,
    val currency: String? = null,
    val credits: Int? = null,
    val subscriptions: ArrayList<String>? = null,
    val bikes: ArrayList<String>? = null,
    val screenName: String? = null,
    val freeSeconds: Int? = null,
    val ticketIds: ArrayList<Int>? = null,
)
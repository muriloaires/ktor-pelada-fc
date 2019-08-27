package model

data class EstablishmentAddress(
    val zipCode: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: Long,
    val longitude: Long
)
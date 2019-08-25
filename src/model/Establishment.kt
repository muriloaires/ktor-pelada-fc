package model

data class Establishment(
    val name: String,
    val address: EstablishmentAdress,
    val sports : List<Sport>
)

data class EstablishmentAdress(
    val zipCode: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: Long,
    val longitude: Long
)
package model

data class Establishment(
    val name : String,
    val addresses : List<EstablishmentAdress>
)

data class EstablishmentAdress(
    val establishment: Establishment,
    val zipCode: String,
    val
)
package web.model.incoming

data class NewEstablishment(
    val name: String,
    val description : String
)

data class NewEstablishmentAddress(
    val zipCode: String,
    val streetAddress: String,
    val city : String,
    val state : String,
    val country : String,
    val latitude : Long,
    val longitude : Long
)
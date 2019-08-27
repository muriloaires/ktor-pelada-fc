package web.model.incoming

import com.google.gson.annotations.SerializedName

data class NewEstablishment(
    @SerializedName("name") val _name: String?,
    @SerializedName("description") val _description: String?,
    @SerializedName("sports") val sports: List<Int>
) {
    val name
        get() = _name ?: throw IllegalArgumentException("name is required")

    val description
        get() = _description ?: throw IllegalArgumentException("description is required")
}

data class EditedEstablishment(val name: String?, val description: String?, val sports : List<Int>)

data class NewEstablishmentAddress(
    @SerializedName("zipCode") val _zipCode: String?,
    @SerializedName("streetAddress") val _streetAddress: String?,
    @SerializedName("city") val _city: String?,
    @SerializedName("state") val _state: String?,
    @SerializedName("country") val _country: String?,
    val latitude: Long,
    val longitude: Long
) {
    val zipCode
        get() = _zipCode ?: throw IllegalArgumentException("zipCode is required")

    val streetAddress
        get() = _streetAddress ?: throw IllegalArgumentException("streetAddress is required")

    val city
        get() = _city ?: throw IllegalArgumentException("city is required")

    val state
        get() = _state ?: throw IllegalArgumentException("state is required")

    val country
        get() = _country ?: throw IllegalArgumentException("country is required")
}

data class EditEstablishmentAddress(
    val zipCode: String?,
    val streetAddress: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val latitude: Long?,
    val longitude: Long?
)


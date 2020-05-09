package web.model.incoming

import com.google.gson.annotations.SerializedName

data class NewEstablishmentCourt(
    @SerializedName("name") val _name: String?,
    @SerializedName("description") val _description: String?,
    @SerializedName("sports") val sports: List<Int>?,
    var courtPhotoUrl: String?
) {
    val name
        get() = _name ?: throw IllegalArgumentException("name is required")

    val description
        get() = _description ?: throw IllegalArgumentException("description is required")
}

data class EditEstablishmentCourt(
    val name: String?,
    val description: String?,
    val isAvailable: Boolean?,
    val sports: List<Int>?,
    var courtPhotoUrl: String?
)
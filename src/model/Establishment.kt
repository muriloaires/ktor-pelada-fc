package model

import model.base.BaseModel
import java.util.*

data class Establishment(
    override val createdAt: Date,
    override val updatedAt: Date,
    val id: Int,
    val name: String,
    val description: String,
    var profilePhotoUrl: String? = null,
    var coverPhotoUrl: String? = null,
    val address: EstablishmentAddress?,
    val sports: List<Sport>,
    val businessHours : List<EstablishmentBusinessHour>
) : BaseModel()


package model

import model.base.BaseModel
import java.util.*

data class Establishment(
    override val createdAt: Date,
    override val updatedAt: Date,
    val id : Int,
    val name: String,
    val description: String,
    val address: EstablishmentAdress?,
    val sports: List<Sport>
) : BaseModel()

data class EstablishmentAdress(
    val zipCode: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: Long,
    val longitude: Long
)
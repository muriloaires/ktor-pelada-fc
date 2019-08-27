package model

import model.base.BaseModel
import java.util.*

data class SportCourt(
    val id: Int,
    override val createdAt: Date,
    override val updatedAt: Date,
    val name: String,
    val description: String,
    val courtPhotoUrl: String? = null,
    val isAvailable: Boolean,
    val sports: List<Sport>
) : BaseModel()

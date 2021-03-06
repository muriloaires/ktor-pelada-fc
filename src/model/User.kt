package model

import io.ktor.auth.Principal
import model.base.BaseModel
import java.util.*

data class User(
    override val createdAt: Date,
    override val updatedAt: Date,
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val loginType: String,
    val isAdvertiser: Boolean,
    var establishments: List<Establishment>? = null,
    var token: String? = null,
    var photoUrl: String? = null
) : BaseModel(), Principal
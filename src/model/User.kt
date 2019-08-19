package model

import io.ktor.auth.Principal
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = text("name")
    val username = text("username")
    val email = text("email")
    val password = text("password")
    val loginType = text("login_type")
    val isAdvertiser = bool("is_advertiser")
}

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val isAdvertiser: Boolean,
    var token: String?,
    var loginType: String
) : Principal

data class NewUser(
    val id: Int?,
    val name: String = "",
    val username: String,
    val email: String,
    val isAdvertiser: Boolean,
    val password: String?,
    val loginType: String
)

enum class LoginType(val value: String) {
    GOOGLE("G"), DEFAULT("D"), FACEBOOK("F")
}
package model

import io.ktor.auth.Principal
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Users : IntIdTable() {
    val name = text("name")
    val username = text("username")
    val email = text("email")
    val password = text("password")
    val loginType = text("login_type")
    val isAdvertiser = bool("is_advertiser")
}

class User(id: EntityID<Int>) : IntEntity(id), Principal {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var username by Users.username
    var email by Users.email
    var passwor by Users.password
    var loginType by Users.loginType
    var isAdvertiser by Users.isAdvertiser

}


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
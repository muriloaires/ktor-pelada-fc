package model

import io.ktor.auth.Principal
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = text("name")
    val email = text("email")
    val password = text("password")
}

data class User(
    val id: Int,
    val name: String,
    val email: String,
    @Transient val password: String,
    var token: String?
) : Principal

data class NewUser(
    val id: Int?,
    val name: String,
    val email: String,
    val password: String
)
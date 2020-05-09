package dao.tables

import dao.base.BaseIntIdTable
import model.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

object Users : BaseIntIdTable() {
    val name = text("name")
    val username = text("username").uniqueIndex()
    val email = text("email")
    val password = text("password")
    val loginType = text("login_type")
    val isAdvertiser = bool("is_advertiser").default(true)
    val photoUrl = text("photo_url").nullable()
}

class UserRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserRow>(Users)

    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt
    var name by Users.name
    var username by Users.username
    var email by Users.email
    var password by Users.password
    var loginType by Users.loginType
    var isAdvertiser by Users.isAdvertiser
    var photoUrl by Users.photoUrl
    val establishments by EstablishmentRow referrersOn Establishments.user
    val reservations by CourtReservationRow referrersOn CourtReservations.user
}

enum class LoginType(val value: String) {
    GOOGLE("G"), DEFAULT("D"), FACEBOOK("F")
}

fun UserRow.toUser() = User(
    this.createdAt.toDate(),
    this.updatedAt.toDate(),
    this.id.value,
    this.name,
    this.username,
    this.email,
    this.loginType,
    this.isAdvertiser,
    photoUrl = this.photoUrl
)
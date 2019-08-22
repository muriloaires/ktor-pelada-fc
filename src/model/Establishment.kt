package model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object Establishments : IntIdTable() {
    val name = text("name")
}

class Establishment(id: EntityID<Int>) : IntEntity(id) {
    var name by Establishments.name
}

object EstablishmentAddresses : Table() {
    val zipCode = text("zip_code")
    val streetAddress = text("street_address")
    val city = text("city")
    val state = text("state")
    val country = text("country")
    val latitude = long("latitude")
    val longitude = long("longitude")
    val establishmentId = integer("establishment_id")
        .uniqueIndex()
        .references(Establishments.id)
}

data class EstablishmentAddress(
    val establishmentId: Int,
    val zipCode: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: Long,
    val longitude: Long
)


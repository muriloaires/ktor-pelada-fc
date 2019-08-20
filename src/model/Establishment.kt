package model

import org.jetbrains.exposed.sql.Table

object Establishments : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = text("name")
}

data class Establishment(
    val id: Int,
    val name: String,
    val establishmentAddress: EstablishmentAddress
)

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
        .references( Establishments.id)
}

data class EstablishmentAddress(
    val establishmentId: Int,
    val zipCode: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: Long,
    val lonngitude: Long
)


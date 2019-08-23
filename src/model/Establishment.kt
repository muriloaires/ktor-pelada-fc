package model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object Establishments : IntIdTable() {
    val name = text("name")
}

class Establishment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Establishment>(Establishments)

    var name by Establishments.name
    val addresses by EstablishmentAddress referrersOn EstablishmentAddresses.establishment
}

object EstablishmentAddresses : IntIdTable() {
    val establishment = reference("establishment", Establishments)
    val zipCode = text("zip_code")
    val streetAddress = text("street_address")
    val city = text("city")
    val state = text("state")
    val country = text("country")
    val latitude = long("latitude")
    val longitude = long("longitude")
}

class EstablishmentAddress(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstablishmentAddress>(EstablishmentAddresses)

    var establishment by Establishment referencedOn EstablishmentAddresses.establishment
    var zipCode by EstablishmentAddresses.zipCode
    var streetAddress by EstablishmentAddresses.streetAddress
    var city by EstablishmentAddresses.city
    var state by EstablishmentAddresses.state
    var country by EstablishmentAddresses.country
    var latitude by EstablishmentAddresses.latitude
    var longitude by EstablishmentAddresses.longitude

}
//    val zipCode: String,
//    val streetAddress: String,
//    val city: String,
//    val state: String,
//    val country: String,
//    val latitude: Long,
//    val longitude: Long


package dao.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Establishments : IntIdTable() {
    val name = text("name")
}

class EstablishmentRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstablishmentRow>(Establishments)

    var name by Establishments.name
    val addresses by EstablishmentAddressRow referrersOn EstablishmentAddresses.establishment
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

class EstablishmentAddressRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstablishmentAddressRow>(EstablishmentAddresses)

    var establishment by EstablishmentRow referencedOn EstablishmentAddresses.establishment
    var zipCode by EstablishmentAddresses.zipCode
    var streetAddress by EstablishmentAddresses.streetAddress
    var city by EstablishmentAddresses.city
    var state by EstablishmentAddresses.state
    var country by EstablishmentAddresses.country
    var latitude by EstablishmentAddresses.latitude
    var longitude by EstablishmentAddresses.longitude

}

package dao.model

import model.Establishment
import model.EstablishmentAdress
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Table

object Establishments : IntIdTable() {
    val name = text("name")
    val user = reference("user", Users)
    val address = reference("adress", EstablishmentAddresses)
}

class EstablishmentRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstablishmentRow>(Establishments)

    var name by Establishments.name
    var user by UserRow referencedOn Establishments.user
    var address by EstablishmentAddressRow referencedOn Establishments.address
    var sports by SportsRow via EstablishmentSports
}

object EstablishmentAddresses : IntIdTable() {

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

    val establishments by EstablishmentRow referrersOn Establishments.address
    var zipCode by EstablishmentAddresses.zipCode
    var streetAddress by EstablishmentAddresses.streetAddress
    var city by EstablishmentAddresses.city
    var state by EstablishmentAddresses.state
    var country by EstablishmentAddresses.country
    var latitude by EstablishmentAddresses.latitude
    var longitude by EstablishmentAddresses.longitude

}

object EstablishmentSports : Table() {
    val establishment = reference("establishment", Establishments).primaryKey(0)
    val sport = reference("sport", Sports).primaryKey(1)
}

fun EstablishmentRow.toEstablishment() =
    Establishment(this.name, this.address.toEstablishmentAddress(), this.sports.map { it.toSport() })

fun EstablishmentAddressRow.toEstablishmentAddress() =
    EstablishmentAdress(
        this.zipCode,
        this.streetAddress,
        this.city,
        this.state,
        this.country,
        this.latitude,
        this.longitude
    )


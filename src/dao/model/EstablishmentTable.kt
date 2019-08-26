package dao.model

import dao.base.BaseIntIdTable
import model.Establishment
import model.EstablishmentAdress
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Establishments : BaseIntIdTable() {
    val name = text("name")
    val description = text("description")
    val user = reference("user", Users)
}

class EstablishmentRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstablishmentRow>(Establishments)

    var createdAt by Establishments.createdAt
    var updatedAt by Establishments.updatedAt
    var description by Establishments.description
    var name by Establishments.name
    var user by UserRow referencedOn Establishments.user
    val addresses by EstablishmentAddressRow referrersOn EstablishmentAddresses.establishment
    var sports by SportsRow via EstablishmentSports
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

object EstablishmentSports : Table() {
    val establishment = reference("establishment", Establishments).primaryKey(0)
    val sport = reference("sport", Sports).primaryKey(1)
}

fun EstablishmentRow.toEstablishment() =
    Establishment(
        this.createdAt.toDate(),
        this.updatedAt.toDate(),
        id.value,
        this.name,
        this.description,
        transaction {
            this@toEstablishment.addresses.toList().map { it.toEstablishmentAddress() }.let {
                if (it.isEmpty()) {
                    null
                } else {
                    it[0]
                }
            }
        },
        transaction {
            this@toEstablishment.sports.map { it.toSport() }
        }
    )

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


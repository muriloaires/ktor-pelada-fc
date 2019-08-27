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
    var sports by SportRow via EstablishmentSports
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




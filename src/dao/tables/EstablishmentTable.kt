package dao.tables

import dao.base.BaseIntIdTable
import model.Establishment
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.transactions.transaction

object Establishments : BaseIntIdTable() {
    val name = text("name")
    val description = text("description")
    val profilePhotoUrl = text("profile_photo_url").nullable()
    val coverPhotoUrl = text("cover_photo_url").nullable()
    val user = reference("user", Users)
}

class EstablishmentRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstablishmentRow>(Establishments)

    var createdAt by Establishments.createdAt
    var updatedAt by Establishments.updatedAt
    var name by Establishments.name
    var description by Establishments.description
    var profilePhotoUrl by Establishments.profilePhotoUrl
    var coverPhotoUrl by Establishments.coverPhotoUrl
    var user by UserRow referencedOn Establishments.user
    val addresses by EstablishmentAddressRow referrersOn EstablishmentAddresses.establishment
    val sportCourts by SportCourtRow referrersOn SportCourts.establishment
    var sports by SportRow via EstablishmentSportsRelation
    val businessHours by EstablishmentBusinessHourRow referrersOn EstablishmentBusinessHours.establishment
}

fun EstablishmentRow.toEstablishment() =
    Establishment(
        this.createdAt.toDate(),
        this.updatedAt.toDate(),
        id.value,
        this.name,
        this.description,
        this.profilePhotoUrl,
        this.coverPhotoUrl,
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
        },

        transaction {
            this@toEstablishment.businessHours.map { it.toEstablishmentBusinessHour() }
        }
    )




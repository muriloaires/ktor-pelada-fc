package dao.tables

import dao.base.BaseIntIdTable
import model.SportCourt
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

object SportCourts : BaseIntIdTable() {
    val name = text("name")
    val description = text("description")
    val courtPhotoUrl = text("court_photo_url").nullable()
    val establishment = reference("establishment", Establishments)
    val isAvailable = bool("is_available").default(true)
}

class SportCourtRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SportCourtRow>(SportCourts)

    var createdAt by SportCourts.createdAt
    var updatedAt by SportCourts.updatedAt
    var name by SportCourts.name
    var description by SportCourts.description
    var courtPhotoUrl by SportCourts.courtPhotoUrl
    var isAvailable by SportCourts.isAvailable
    var establishment by EstablishmentRow referencedOn SportCourts.establishment
    var sports by SportRow via CourtSportsRelation
}

fun SportCourtRow.toSportCourt() = SportCourt(
    this.id.value,
    this.createdAt.toDate(),
    this.updatedAt.toDate(),
    this.name,
    this.description,
    this.courtPhotoUrl,
    this.isAvailable,
    this.sports.toList().map { it.toSport() }
)
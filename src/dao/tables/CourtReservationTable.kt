package dao.tables

import dao.base.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

object CourtReservations : BaseIntIdTable() {
    val startAt = datetime("start_at")
    val endAt = datetime("end_at")
    val status = integer("status")
    val price = float("price")
    val user = reference("user", Users)
    val sportCourt = reference("sport_court", SportCourts)

}

class CourtReservationRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CourtReservationRow>(CourtReservations)

    var startAt by CourtReservations.startAt
    var endAt by CourtReservations.endAt
    var price by CourtReservations.price
    var status by CourtReservations.status
    var user by UserRow referencedOn CourtReservations.user
    var sportCourt by SportCourtRow referencedOn CourtReservations.sportCourt
}
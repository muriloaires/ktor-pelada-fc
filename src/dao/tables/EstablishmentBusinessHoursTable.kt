package dao.tables

import model.EstablishmentBusinessHour
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object EstablishmentBusinessHours : IntIdTable() {
    val isOpen = bool("is_open").default(true)
    val openingTime = datetime("opening_time")
    val closingTime = datetime("closing_time")
    val dayOfWeek = integer("day_of_week")
    val establishment = reference("establishment", Establishments)
}

class EstablishmentBusinessHourRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstablishmentBusinessHourRow>(EstablishmentBusinessHours)

    var isOpen by EstablishmentBusinessHours.isOpen
    var openingTime by EstablishmentBusinessHours.openingTime
    var closingTime by EstablishmentBusinessHours.closingTime
    var dayOfWeek by EstablishmentBusinessHours.dayOfWeek
    var establishment by EstablishmentRow referencedOn EstablishmentBusinessHours.establishment
}

fun EstablishmentBusinessHourRow.toEstablishmentBusinessHour() = EstablishmentBusinessHour(
    this.isOpen,
    this.openingTime.toDate(),
    this.closingTime.toDate(),
    this.dayOfWeek
)
package dao.services

import dao.EstablishmentBusinessHourDAO
import dao.tables.EstablishmentBusinessHourRow
import dao.tables.EstablishmentRow
import org.joda.time.DateTime

class EstablishmentBusinessHourServiceDAO : EstablishmentBusinessHourDAO {
    override fun createInitialBusinessHoursForNewEstablishment(establishmentRow: EstablishmentRow) {
        for (i in 1 until 8) {
            EstablishmentBusinessHourRow.new {
                this.establishment = establishmentRow
                this.openingTime = DateTime.now()
                this.closingTime = DateTime.now()
                this.dayOfWeek = i
            }
        }
    }
}
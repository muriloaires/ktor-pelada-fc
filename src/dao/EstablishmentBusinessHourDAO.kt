package dao

import dao.tables.EstablishmentRow

interface EstablishmentBusinessHourDAO {

    fun createInitialBusinessHoursForNewEstablishment(establishmentRow: EstablishmentRow)

}
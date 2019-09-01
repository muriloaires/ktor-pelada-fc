package dao

import dao.factory.DatabaseFactory
import dao.tables.EstablishmentRow
import dao.tables.SportCourtRow
import web.model.incoming.*

interface EstablishmentDAO {

    suspend fun getAllEstablishments(): List<EstablishmentRow> {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.all().toList()
        }
    }

    suspend fun getEstablishment(establishmentId: Int): EstablishmentRow?

    suspend fun getUserEstablishments(userId: Int): List<EstablishmentRow>

    suspend fun createEstablishment(userId: Int, newEstablishment: NewEstablishment): EstablishmentRow?

    suspend fun deleteEstablishment(establishmentId: Int): Boolean

    suspend fun updateAddress(establishmentId: Int, editedAddress: EditEstablishmentAddress): EstablishmentRow?

    suspend fun createAddress(establishmentId: Int, newAddress: NewEstablishmentAddress): EstablishmentRow?

    suspend fun updateEstablishment(establishmentId: Int, editedEstablishment: EditedEstablishment): EstablishmentRow?

    suspend fun updateEstablishmentBusinessHours(
        establishmentId: Int,
        newBusinessHour: NewEstablishmentBusinessHour,
        dayOfWeek: Int
    ): EstablishmentRow?


}
package dao

import dao.tables.EstablishmentRow
import dao.tables.SportCourtRow
import web.model.incoming.EditEstablishmentAddress
import web.model.incoming.EditedEstablishment
import web.model.incoming.NewEstablishment
import web.model.incoming.NewEstablishmentAddress

interface EstablishmentDAO {
    suspend fun getAllEstablishments(): List<EstablishmentRow>

    suspend fun findEstablishmentById(id: Int): EstablishmentRow?

    suspend fun getAllEstablishmentsFromUser(userId: Int): List<EstablishmentRow?>

    suspend fun createEstablishment(userId: Int, newEstablishment: NewEstablishment): EstablishmentRow?

    suspend fun delete(establishmentId: Int): Boolean

    suspend fun updateAddress(establishmentId: Int, editedAddress: EditEstablishmentAddress): EstablishmentRow?

    suspend fun createAddress(establishmentId: Int, newAddress: NewEstablishmentAddress): EstablishmentRow?

    suspend fun updateEstablishment(establishmentId: Int, editedEstablishment: EditedEstablishment): EstablishmentRow?

}
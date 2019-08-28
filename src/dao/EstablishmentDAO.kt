package dao

import dao.tables.EstablishmentRow
import dao.tables.SportCourtRow
import web.model.incoming.EditEstablishmentAddress
import web.model.incoming.EditedEstablishment
import web.model.incoming.NewEstablishment
import web.model.incoming.NewEstablishmentAddress

interface EstablishmentDAO {

    suspend fun getEstablishment(establishmentId: Int): EstablishmentRow?

    suspend fun getUserEstablishments(userId: Int): List<EstablishmentRow>

    suspend fun createEstablishment(userId: Int, newEstablishment: NewEstablishment): EstablishmentRow?

    suspend fun deleteEstablishment(establishmentId: Int): Boolean

    suspend fun updateAddress(establishmentId: Int, editedAddress: EditEstablishmentAddress): EstablishmentRow?

    suspend fun createAddress(establishmentId: Int, newAddress: NewEstablishmentAddress): EstablishmentRow?

    suspend fun updateEstablishment(establishmentId: Int, editedEstablishment: EditedEstablishment): EstablishmentRow?

}
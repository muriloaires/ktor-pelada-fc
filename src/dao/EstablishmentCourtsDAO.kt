package dao

import dao.tables.SportCourtRow
import web.model.incoming.EditEstablishmentCourt
import web.model.incoming.NewEstablishmentCourt

interface EstablishmentCourtsDAO {

    suspend fun getAllEstablishmentCourts(establishmentId: Int): List<SportCourtRow>

    suspend fun createCourt(establishmentId: Int, newCourt: NewEstablishmentCourt): SportCourtRow?

    suspend fun updateCourt( courtId: Int, editedCourtBody: EditEstablishmentCourt): SportCourtRow?

    suspend fun deleteCourt(courtId: Int): Boolean

}
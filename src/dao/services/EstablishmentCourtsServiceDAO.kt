package dao.services

import dao.EstablishmentCourtsDAO
import dao.tables.SportCourtRow
import web.model.incoming.EditEstablishmentCourt
import web.model.incoming.NewEstablishmentCourt

class EstablishmentCourtsServiceDAO : EstablishmentCourtsDAO{
    override suspend fun getAllEstablishmentCourts(establishmentId: Int): List<SportCourtRow> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createCourt(establishmentId: Int, newCourt: NewEstablishmentCourt): SportCourtRow? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateCourt(
        establishmentId: Int,
        courtId: Int,
        editedCourtBody: EditEstablishmentCourt
    ): SportCourtRow? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteCourt(establishmentId: Int, courtId: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
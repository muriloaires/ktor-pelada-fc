package dao.services

import dao.EstablishmentDAO
import dao.factory.DatabaseFactory
import dao.model.EstablishmentRow
import dao.model.Establishments
import dao.model.toEstablishment
import model.Establishment

class EstablishmentServiceDAO : EstablishmentDAO {
    override suspend fun getAllEstablishments(): List<Establishment> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun findEstablishmentById(id: Int): Establishment? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAllEstablishmentsFromUser(userId: Int): List<Establishment> {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.find {
                Establishments.user eq userId
            }.map { establishmentRow ->
                establishmentRow.toEstablishment()
            }
        }
    }
}
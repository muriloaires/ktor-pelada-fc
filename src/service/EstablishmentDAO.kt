package service

import model.EstablishmentAddresses
import model.Establishments
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.selectAll

interface EstablishmentDAO {
    suspend fun getAll(): Query
}

class EstablishmentDAOImpl : EstablishmentDAO {

    override suspend fun getAll(): Query = DatabaseFactory.dbQuery {
        (Establishments innerJoin EstablishmentAddresses).selectAll()
    }

}
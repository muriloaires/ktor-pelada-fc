package service

import model.NewPartida
import model.Partida
import model.Partidas
import service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class PartidaService {

    suspend fun getAllPartidas(): List<Partida> = dbQuery {
        Partidas.selectAll().map { toPartida(it) }
    }

    suspend fun getPartida(id: Int): Partida? = dbQuery {
        Partidas.select {
            (Partidas.id eq id)
        }.mapNotNull { toPartida(it) }.singleOrNull()
    }

    suspend fun updatePartida(partida: NewPartida): Partida? {
        val id = partida.id
        id?.let {
            dbQuery {
                Partidas.update({ Partidas.id eq id }) {
                    it[dataRealizacao] = System.currentTimeMillis()
                }
            }
            return getPartida(id)
        } ?: run {
            return addPartida(partida)
        }
    }

    suspend fun addPartida(partida: NewPartida): Partida {
        var key = 0
        dbQuery {
            key = (Partidas.insert {
                it[dataRealizacao] = System.currentTimeMillis()
            } get Partidas.id)
        }
        return getPartida(key)!!
    }

    suspend fun deletePartida(id: Int) = dbQuery {
        Partidas.deleteWhere { Partidas.id eq id } > 0
    }

    private fun toPartida(row: ResultRow): Partida =
        Partida(
            id = row[Partidas.id],
            dataRealizacao = row[Partidas.dataRealizacao]
        )
}
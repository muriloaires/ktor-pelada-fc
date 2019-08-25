package dao

import model.Establishment

interface EstablishmentDAO {
    suspend fun getAllEstablishments(): List<Establishment>

    suspend fun findEstablishmentById(id: Int): Establishment?
    suspend fun getAllEstablishmentsFromUser(userId: Int): List<Establishment>

}
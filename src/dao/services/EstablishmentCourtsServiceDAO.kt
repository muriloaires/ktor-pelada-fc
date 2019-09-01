package dao.services

import dao.EstablishmentCourtsDAO
import dao.factory.DatabaseFactory
import dao.tables.EstablishmentRow
import dao.tables.SportCourtRow
import dao.tables.SportCourts
import dao.tables.SportRow
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.and
import org.joda.time.DateTime
import web.model.incoming.EditEstablishmentCourt
import web.model.incoming.NewEstablishmentCourt

class EstablishmentCourtsServiceDAO : EstablishmentCourtsDAO {

    override suspend fun getAllEstablishmentCourts(establishmentId: Int): List<SportCourtRow> {
        return DatabaseFactory.dbQuery {
            SportCourtRow.find {
                SportCourts.establishment eq establishmentId
            }.toList()
        }
    }

    override suspend fun createCourt(establishmentId: Int, newCourt: NewEstablishmentCourt): SportCourtRow? {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.findById(establishmentId)?.let {
                SportCourtRow.new {
                    establishment = it
                    name = newCourt.name
                    description = newCourt.description
                    sports = SizedCollection(mutableListOf<SportRow>().apply {
                        newCourt.sports?.forEach {
                            SportRow.findById(it)?.let { row ->
                                add(row)
                            }
                        }
                    })
                }
            }
        }
    }

    override suspend fun updateCourt(
        courtId: Int,
        editedCourtBody: EditEstablishmentCourt
    ): SportCourtRow? {
        return DatabaseFactory.dbQuery {
            SportCourtRow.findById(courtId)?.apply {
                editedCourtBody.name?.let {
                    name = it
                    updatedAt = DateTime.now()
                }
                editedCourtBody.description?.let {
                    description = it
                    updatedAt = DateTime.now()
                }
                editedCourtBody.isAvailable?.let {
                    isAvailable = it
                    updatedAt = DateTime.now()
                }

                editedCourtBody.sports?.let { list ->
                    SizedCollection(mutableListOf<SportRow>().apply {
                        list.forEach {
                            SportRow.findById(it)?.let { row ->
                                add(row)
                            }
                        }
                    })
                    updatedAt = DateTime.now()
                }
            }
        }

    }

    override suspend fun deleteCourt(courtId: Int): Boolean {
        return DatabaseFactory.dbQuery {
            SportCourtRow.findById(courtId)?.let {
                it.delete()
                true
            } ?: run {
                false
            }
        }
    }
}
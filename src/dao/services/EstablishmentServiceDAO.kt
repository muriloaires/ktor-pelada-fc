package dao.services

import dao.EstablishmentDAO
import dao.UserDAO
import dao.factory.DatabaseFactory
import dao.model.*
import model.Establishment
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.deleteWhere
import org.joda.time.DateTime
import web.model.incoming.NewEstablishment
import web.model.incoming.NewEstablishmentAddress

class EstablishmentServiceDAO : EstablishmentDAO {
    private val userDAO: UserDAO = UserServiceDAO()
    override suspend fun getAllEstablishments(): List<EstablishmentRow> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun findEstablishmentById(id: Int): EstablishmentRow? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAllEstablishmentsFromUser(userId: Int): List<EstablishmentRow?> {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.find {
                Establishments.user eq userId
            }.toList()
        }
    }

    override suspend fun createEstablishment(userId: Int, newEstablishment: NewEstablishment): EstablishmentRow? {
        return DatabaseFactory.dbQuery {
            val userRow = userDAO.findUserById(userId)
            userRow?.let {
                EstablishmentRow.new {
                    createdAt = DateTime.now()
                    updatedAt = DateTime.now()
                    user = userRow
                    name = newEstablishment.name
                    description = newEstablishment.description
                }
            }
        }
    }

    override suspend fun delete(establishmentId: Int): Boolean {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.findById(establishmentId)?.let {
                it.addresses.map { addressRow -> addressRow.delete() }
                EstablishmentSports.deleteWhere {
                    EstablishmentSports.establishment eq establishmentId
                }
                it.delete()
                true
            } ?: run {
                false
            }
        }
    }

    override suspend fun updateAddress(establishmentId: Int, newAddress: NewEstablishmentAddress): EstablishmentRow? {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.findById(establishmentId)?.let { establishmentRow ->
                EstablishmentAddressRow.findById(0)?.let { establishmentAddressRow ->
                    establishmentAddressRow.establishment = establishmentRow
                    establishmentAddressRow.city = newAddress.city
                    establishmentAddressRow.state = newAddress.state
                    establishmentAddressRow.country = newAddress.country
                    establishmentAddressRow.zipCode = newAddress.zipCode
                    establishmentAddressRow.latitude = newAddress.latitude
                    establishmentAddressRow.longitude = newAddress.longitude
                    establishmentAddressRow.streetAddress = newAddress.streetAddress
                } ?: run {
                    EstablishmentAddressRow.new(0) {
                        establishment = establishmentRow
                        city = newAddress.city
                        state = newAddress.state
                        country = newAddress.country
                        zipCode = newAddress.zipCode
                        latitude = newAddress.latitude
                        longitude = newAddress.longitude
                        streetAddress = newAddress.streetAddress
                    }
                }
            }
            EstablishmentRow.findById(establishmentId)
        }
    }
}
package dao.services

import dao.EstablishmentBusinessHourDAO
import dao.EstablishmentDAO
import dao.UserDAO
import dao.factory.DatabaseFactory
import dao.tables.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.joda.time.DateTime
import web.model.incoming.*

class EstablishmentServiceDAO : EstablishmentDAO {
    private val userDAO: UserDAO = UserServiceDAO()
    private val establishmentBusinessHoursDAO: EstablishmentBusinessHourDAO = EstablishmentBusinessHourServiceDAO()

    override suspend fun getEstablishment(establishmentId: Int): EstablishmentRow? {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.findById(establishmentId)
        }
    }

    override suspend fun getUserEstablishments(userId: Int): List<EstablishmentRow> {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.find {
                Establishments.user eq userId
            }.toList()
        }
    }

    override suspend fun createEstablishment(userId: Int, newEstablishment: NewEstablishment): EstablishmentRow? {
        return DatabaseFactory.dbQuery {
            val sportsList = mutableListOf<SportRow>()
            newEstablishment.sports?.forEach {
                SportRow.findById(it)?.let { row ->
                    sportsList.add(row)
                }
            }
            userDAO.findUserById(userId)?.let {
                EstablishmentRow.new {
                    createdAt = DateTime.now()
                    updatedAt = DateTime.now()
                    user = it
                    name = newEstablishment.name
                    description = newEstablishment.description
                }.apply {
                    this.sports = SizedCollection(sportsList)
                    establishmentBusinessHoursDAO.createInitialBusinessHoursForNewEstablishment(this)
                }
            }
        }
    }

    override suspend fun deleteEstablishment(establishmentId: Int): Boolean {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.findById(establishmentId)?.let {
                it.addresses.map { addressRow -> addressRow.delete() }
                EstablishmentSportsRelation.deleteWhere {
                    EstablishmentSportsRelation.establishment eq establishmentId
                }
                it.delete()
                true
            } ?: run {
                false
            }
        }
    }

    override suspend fun updateAddress(
        establishmentId: Int,
        editedAddress: EditEstablishmentAddress
    ): EstablishmentRow? {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.findById(establishmentId)?.apply {
                var updated = false
                EstablishmentAddressRow.find { EstablishmentAddresses.establishment eq establishmentId }.singleOrNull()
                    ?.apply {
                        editedAddress.city?.let {
                            city = it
                            updated = true
                        }
                        editedAddress.state?.let {
                            state = it
                            updated = true
                        }
                        editedAddress.country?.let {
                            country = it
                            updated = true
                        }
                        editedAddress.zipCode?.let {
                            zipCode = it
                            updated = true
                        }
                        editedAddress.latitude?.let {
                            latitude = it
                            updated = true
                        }
                        editedAddress.longitude?.let {
                            longitude = it
                            updated = true
                        }
                        editedAddress.streetAddress?.let {
                            streetAddress = it
                        }
                    }
                if (updated) {
                    this@apply.updatedAt = DateTime.now()
                }
            }

        }
    }

    override suspend fun createAddress(establishmentId: Int, newAddress: NewEstablishmentAddress): EstablishmentRow? {
        return DatabaseFactory.dbQuery {
            EstablishmentRow.findById(establishmentId)?.let { establishmentRow ->
                EstablishmentAddressRow.find { EstablishmentAddresses.establishment eq establishmentId }
                    .forEach { it.delete() }
                EstablishmentAddressRow.new {
                    establishment = establishmentRow
                    newAddress.city.let { city = it }
                    newAddress.state.let { state = it }
                    newAddress.country.let { country = it }
                    newAddress.zipCode.let { zipCode = it }
                    newAddress.latitude.let { latitude = it }
                    newAddress.longitude.let { longitude = it }
                    newAddress.streetAddress.let { streetAddress = it }
                }

            }
            EstablishmentRow.findById(establishmentId)
        }
    }

    override suspend fun updateEstablishment(
        establishmentId: Int,
        editedEstablishment: EditedEstablishment
    ): EstablishmentRow? {
        return DatabaseFactory.dbQuery {
            val sportsList = mutableListOf<SportRow>()
            editedEstablishment.sports.forEach {
                SportRow.findById(it)?.let { row ->
                    sportsList.add(row)
                }
            }
            EstablishmentRow.findById(establishmentId)?.apply {
                var updated = false
                editedEstablishment.name?.let {
                    name = it
                    updated = true
                }

                editedEstablishment.description?.let {
                    description = it
                    updated = true
                }
                if (sportsList.isNotEmpty()) {
                    sports = SizedCollection(sportsList)
                    updated = true
                }
                if (updated) {
                    updatedAt = DateTime.now()
                }
            }
        }

    }

    override suspend fun updateEstablishmentBusinessHours(
        establishmentId: Int,
        newBusinessHour: NewEstablishmentBusinessHour,
        dayOfWeek: Int
    ): EstablishmentRow? {
        require(dayOfWeek in 1..7) { "dayOfWeek must be between 1 and 7" }
        return DatabaseFactory.dbQuery {
            EstablishmentBusinessHourRow.find {
                (EstablishmentBusinessHours.establishment eq establishmentId) and (EstablishmentBusinessHours.dayOfWeek eq dayOfWeek)
            }.singleOrNull()?.let { businessRow ->
                businessRow.isOpen = newBusinessHour.isOpen
                newBusinessHour.openingTime?.let {
                    businessRow.openingTime = DateTime(it)
                }
                newBusinessHour.closingTime?.let {
                    businessRow.closingTime = DateTime(it)
                }
            }

            EstablishmentRow.findById(establishmentId)?.apply {
                updatedAt = DateTime.now()
            }
        }

    }
}
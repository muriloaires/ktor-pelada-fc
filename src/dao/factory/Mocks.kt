package dao.factory

import dao.services.EstablishmentBusinessHourServiceDAO
import dao.tables.*
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import security.Hash

object Mocks {

    fun mock() {
        val sports = mockSports()
        val establishment = mockEstablishment(mockUser())
        mockSportCourts(establishment, sports)
        mockAddress(establishment)
        mockEstablishmentSports(establishment, sports)
    }

    private fun mockSports(): List<SportRow> {
        return mutableListOf<SportRow>().apply {
            transaction {
                add(SportRow.new {
                    name = "Futebol"
                    imageThumbUrl = "imageThumUrl"
                    imageMediumUrl = "imageMediuUrl"
                    imageLargeUrl = "imageLargeUrl"
                })
            }

            transaction {
                add(SportRow.new {
                    name = "Basquete"
                    imageThumbUrl = "imageThumUrl"
                    imageMediumUrl = "imageMediuUrl"
                    imageLargeUrl = "imageLargeUrl"
                })
            }
            transaction {
                add(SportRow.new {
                    name = "Volei"
                    imageThumbUrl = "imageThumUrl"
                    imageMediumUrl = "imageMediuUrl"
                    imageLargeUrl = "imageLargeUrl"
                })
            }
        }


    }

    private fun mockUser(): UserRow {
        return transaction {
            UserRow.new {
                createdAt = DateTime()
                updatedAt = DateTime()
                name = "Murilo"
                username = "muriloaires2"
                email = "murilo2@gmail.com"
                isAdvertiser = true
                loginType = LoginType.GOOGLE.value
                password = Hash.sha256("123456")
            }
        }
    }

    private fun mockEstablishment(
        userRow: UserRow
    ): EstablishmentRow {
        return transaction {
            EstablishmentRow.new {
                createdAt = DateTime.now()
                updatedAt = DateTime.now()
                name = "Estabelecimento 1"
                description = "description"
                profilePhotoUrl = "profile pic"
                coverPhotoUrl = "cover pic"
                user = userRow
            }.apply {
                EstablishmentBusinessHourServiceDAO().createInitialBusinessHoursForNewEstablishment(this)
            }
        }
    }

    private fun mockSportCourts(establishmentRow: EstablishmentRow, sports: List<SportRow>) {
        val sportCourtRow: SportCourtRow = transaction {
            SportCourtRow.new {
                createdAt = DateTime.now()
                updatedAt = DateTime.now()
                establishment = establishmentRow
                name = "Quadra de futebol 1"
                description = "Descrição quadra de futebol 1"
            }
        }
        val sportCourtRow2: SportCourtRow = transaction {
            SportCourtRow.new {
                createdAt = DateTime.now()
                updatedAt = DateTime.now()
                establishment = establishmentRow
                name = "Quadra de futebol 2"
                description = "Descrição quadra de futebol 2"
            }
        }
        transaction {
            sportCourtRow.sports = SizedCollection(sports)
            sportCourtRow2.sports = SizedCollection(sports)
        }


    }

    private fun mockEstablishmentSports(establishmentRow: EstablishmentRow, sports: List<SportRow>) {
        transaction {
            establishmentRow.sports = SizedCollection(sports)
        }
    }

    private fun mockAddress(establishmentRow: EstablishmentRow): EstablishmentAddressRow {
        return transaction {
            EstablishmentAddressRow.new(0) {

                establishment = establishmentRow
                zipCode = "123"
                streetAddress = "Street Address"
                city = "City"
                state = "State"
                country = "Country"
                latitude = 239043490L
                longitude = 9392932L
            }
        }
    }


}
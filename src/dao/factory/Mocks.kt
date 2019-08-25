package dao.factory

import dao.model.*
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import security.Hash

object Mocks {

    fun mock() {
        val sports = mockSports()
        val establishment = mockEstablishment(mockUser(), mockAddress())
        mockEstablishmentSports(establishment, sports)
    }

    private fun mockSports(): List<SportsRow> {
        return mutableListOf<SportsRow>().apply {
            transaction {
                add(SportsRow.new {
                    name = "Futebol"
                    imageThumbUrl = "imageThumUrl"
                    imageMediumUrl = "imageMediuUrl"
                    imageLargeUrl = "imageLargeUrl"
                })
            }

            transaction {
                add(SportsRow.new {
                    name = "Basquete"
                    imageThumbUrl = "imageThumUrl"
                    imageMediumUrl = "imageMediuUrl"
                    imageLargeUrl = "imageLargeUrl"
                })
            }
            transaction {
                add(SportsRow.new {
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
        userRow: UserRow,
        establishmentAddressRow: EstablishmentAddressRow
    ): EstablishmentRow {
        return transaction {
            EstablishmentRow.new {
                name = "Estabelecimento 1"
                user = userRow
                address = establishmentAddressRow
            }
        }
    }

    private fun mockEstablishmentSports(establishmentRow: EstablishmentRow, sports: List<SportsRow>) {
        transaction {
            establishmentRow.sports = SizedCollection(sports)
        }
    }

    private fun mockAddress(): EstablishmentAddressRow {
        return transaction {
            EstablishmentAddressRow.new {
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
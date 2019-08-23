package service

import security.Hash
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        // Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        Database.connect(hikari())
        transaction {
            create(Partidas, Users, Establishments, EstablishmentAddresses)
            createMock()
        }
    }

    private fun createMock() {

        User.new {
            name = "Murilo"
            username = "muriloaires"
            email = "murilo1@gmail.com"
            isAdvertiser = false
            loginType = LoginType.DEFAULT.value
            password = Hash.sha256("123456")
        }

        User.new {
            name = "Murilo"
            username = "muriloaires2"
            email = "murilo2@gmail.com"
            isAdvertiser = true
            loginType = LoginType.GOOGLE.value
            password = Hash.sha256("123456")
        }

        val establishmentData = Establishment.new {
            name = "Estabelecimento 1"
        }

        EstablishmentAddress.new {
            establishment = establishmentData
            zipCode = "123"
            streetAddress = "Street Address"
            city = "City"
            state = "State"
            country = "Country"
            latitude = 239043490L
            longitude = 9392932L
        }

    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

}
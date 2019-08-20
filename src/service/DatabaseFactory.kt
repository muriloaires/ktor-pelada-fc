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
        Users.insert {
            it[name] = "Murilo"
            it[username] = "muriloaires"
            it[email] = "murilo@gmail.com"
            it[isAdvertiser] = false
            it[loginType] = LoginType.DEFAULT.value
            it[password] = Hash.sha256("123456")
        }

        Users.insert {
            it[name] = "Murilo"
            it[username] = "muriloaires2"
            it[email] = "murilo2@gmail.com"
            it[isAdvertiser] = true
            it[loginType] = LoginType.GOOGLE.value
            it[password] = Hash.sha256("123456")
        }

        Establishments.insert {
            it[id] = 1
            it[name] = "Estabelecimento 1"
        }

        EstablishmentAddresses.insert {
            it[establishmentId] = 1
            it[zipCode] = "234"
            it[streetAddress] = "iojeia"
            it[city] = "Cidade"
            it[state] = "Estado"
            it[country] = "País"
            it[latitude] = 123456879L
            it[longitude] = 564321654L
        }

        Partidas.insert {
            it[dataRealizacao] = System.currentTimeMillis()
        }
        Partidas.insert {
            it[dataRealizacao] = System.currentTimeMillis() - 10000
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

    suspend fun <T> dbQuery(
        block: () -> T
    ): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

}
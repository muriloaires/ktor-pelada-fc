package dao.factory

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dao.tables.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
//         Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        Database.connect(hikari())
        transaction {
            create(
                Users,
                Sports,
                Establishments,
                EstablishmentAddresses,
                EstablishmentSportsRelation,
                SportCourts,
                CourtSportsRelation
            )
        }
        Mocks.mock()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.ds.PGSimpleDataSource"
        config.username = "postgres"
        config.password = "eldorado"
        config.jdbcUrl = "jdbc:postgresql://localhost:5432/batata_pamonha_tila"
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
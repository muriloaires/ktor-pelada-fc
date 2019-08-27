package dao.model

import org.jetbrains.exposed.sql.Table

object EstablishmentSports : Table() {
    val establishment = reference("establishment", Establishments).primaryKey(0)
    val sport = reference("sport", Sports).primaryKey(1)
}
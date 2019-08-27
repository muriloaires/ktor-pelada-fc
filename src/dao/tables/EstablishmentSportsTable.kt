package dao.tables

import org.jetbrains.exposed.sql.Table

object EstablishmentSportsRelation : Table() {
    val establishment = reference("establishment", Establishments).primaryKey(0)
    val sport = reference("sport", Sports).primaryKey(1)
}
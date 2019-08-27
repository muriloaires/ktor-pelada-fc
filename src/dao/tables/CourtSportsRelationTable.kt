package dao.tables

import org.jetbrains.exposed.sql.Table

object CourtSportsRelation : Table() {
    val court = reference("court", SportCourts).primaryKey(0)
    val sport = reference("sport", Sports).primaryKey(1)
}
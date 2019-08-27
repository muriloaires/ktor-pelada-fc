package dao.model

import org.jetbrains.exposed.sql.Table

object CourtSports : Table() {
    val court = reference("court", SportCourts).primaryKey(0)
    val sport = reference("sport", Sports).primaryKey(1)
}
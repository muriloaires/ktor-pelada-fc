package dao.tables

import org.jetbrains.exposed.dao.IntIdTable

object EstablishmentConfig : IntIdTable() {
    var establishment = reference("establishment",Establishments).uniqueIndex()

}
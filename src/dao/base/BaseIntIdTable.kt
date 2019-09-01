package dao.base

import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.DateTime

open class BaseIntIdTable : IntIdTable() {
    val createdAt = datetime("created_at").default(DateTime.now())
    val updatedAt = datetime("updated_at").default(DateTime.now())
}
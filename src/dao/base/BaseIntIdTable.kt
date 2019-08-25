package dao.base

import org.jetbrains.exposed.dao.IntIdTable

open class BaseIntIdTable : IntIdTable() {
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
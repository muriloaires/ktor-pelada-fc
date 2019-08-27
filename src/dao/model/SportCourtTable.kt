package dao.model

import dao.base.BaseIntIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

object SportCourts  : BaseIntIdTable(){
    val name = text("name")
    val establishment = reference("establishment", Establishments)
}

class SportCourtRow(id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<SportCourtRow>(SportCourts)

    var createdAt by SportCourts.createdAt
    var updatedAt by SportCourts.updatedAt
    var name by SportCourts.name
    var establishment by SportCourtRow referencedOn SportCourts.establishment
    var sports by SportRow via CourtSports
}
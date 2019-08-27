package dao.model

import model.Sport
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Sports : IntIdTable() {
    val name = text("name")
    val imageThumbUrl = text("image_thumb_url")
    val imageMediumUrl = text("image_medium_url")
    val imageLargeUrl = text("image_large_url")
}

class SportRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SportRow>(Sports)

    var name by Sports.name
    var imageThumbUrl by Sports.imageThumbUrl
    var imageMediumUrl by Sports.imageMediumUrl
    var imageLargeUrl by Sports.imageLargeUrl
}

fun SportRow.toSport() =
    Sport(this.id.value, this.name, this.imageThumbUrl, this.imageMediumUrl, this.imageLargeUrl)
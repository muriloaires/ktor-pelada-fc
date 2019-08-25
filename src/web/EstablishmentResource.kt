package web

import dao.EstablishmentDAO
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import util.user

fun Route.establishment(establishmentDAO: EstablishmentDAO) {

    authenticate {

        get("/user/establishments") {
            val userId = call.user!!.id
            val establishments = establishmentDAO.getAllEstablishmentsFromUser(userId)
            call.respond(HttpStatusCode.OK, establishments)
        }
    }

}
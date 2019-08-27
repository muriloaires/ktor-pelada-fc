package web

import dao.EstablishmentDAO
import dao.model.toEstablishment
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import util.extensions.toErrorResponse
import util.user
import web.model.incoming.EditedEstablishment
import web.model.incoming.NewEstablishment

fun Route.establishment(establishmentDAO: EstablishmentDAO) {

    authenticate {

        /**
         * return all establishments from the authenticated user
         */
        get("/user/establishments") {
            val userId = call.user!!.id
            val establishments = establishmentDAO.getAllEstablishmentsFromUser(userId)
            call.respond(HttpStatusCode.OK, establishments.map { it?.toEstablishment() })
        }

        /**
         * Create a establishment to the authenticated user
         */
        post("user/establishments") {
            val body = call.receive<NewEstablishment>()
            try {
                val newEstablishment = establishmentDAO.createEstablishment(call.user!!.id, body)
                newEstablishment?.let {
                    call.respond(HttpStatusCode.OK, newEstablishment.toEstablishment())
                } ?: run {
                    call.respond(HttpStatusCode.NotFound)
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
            }
        }

        /**
         * Update the establishment fields
         */
        patch("user/establishments/{establishmentId}") {
            val establishmentId = call.parameters["establishmentId"]
            establishmentId?.let {
                val editedEstablishmentBody = call.receive<EditedEstablishment>()
                val editedEstablishment =
                    establishmentDAO.updateEstablishment(establishmentId.toInt(), editedEstablishmentBody)
                editedEstablishment?.let {
                    call.respond(HttpStatusCode.OK, editedEstablishment)
                } ?: run {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }


        /**
         * Delete and establishment
         */
        delete("user/establishments/{establishmentId}") {
            val establishmentId = call.parameters["establishmentId"]
            establishmentId?.let {
                val deleted = establishmentDAO.delete(establishmentId.toInt())
                call.respond(if (deleted) HttpStatusCode.OK else HttpStatusCode.NotFound)
            }
        }
    }

}
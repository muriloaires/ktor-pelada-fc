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
import web.model.incoming.EditEstablishmentAddress
import web.model.incoming.NewEstablishment
import web.model.incoming.NewEstablishmentAddress

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
        post("user/establishment") {
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
         * Add the address field of a establishment
         */
        post("user/establishment/{establishmentId}/address") {
            val establishmentId = call.parameters["establishmentId"]
            val newAddress = call.receive<NewEstablishmentAddress>()
            try {
                establishmentId?.let {
                    val updatedEstablishment = establishmentDAO.setAddress(establishmentId.toInt(), newAddress)
                    updatedEstablishment?.let {
                        call.respond(HttpStatusCode.OK, updatedEstablishment.toEstablishment())
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
            }
        }

        /**
         * Edit the address field of a establishment
         */
        patch("user/establishment/{establishmentId}/address") {
            val establishmentId = call.parameters["establishmentId"]
            val newAddress = call.receive<EditEstablishmentAddress>()
            establishmentId?.let {
                val updatedEstablishment = establishmentDAO.updateAddress(establishmentId.toInt(), newAddress)
                updatedEstablishment?.let {
                    call.respond(HttpStatusCode.OK, updatedEstablishment.toEstablishment())
                } ?: run {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }


        /**
         * Delete and establishment
         */
        delete("user/establishment/{establishmentId}") {
            val establishmentId = call.parameters["establishmentId"]
            establishmentId?.let {
                val deleted = establishmentDAO.delete(establishmentId.toInt())
                call.respond(if (deleted) HttpStatusCode.OK else HttpStatusCode.NotFound)
            }
        }
    }

}
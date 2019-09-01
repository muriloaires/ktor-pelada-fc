package web

import dao.EstablishmentDAO
import dao.tables.toEstablishment
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.patch
import io.ktor.routing.post
import io.ktor.routing.route
import util.extensions.toErrorResponse
import web.model.incoming.EditEstablishmentAddress
import web.model.incoming.NewEstablishmentAddress

fun Route.establishmentAddress(establishmentDAO: EstablishmentDAO) {

    authenticate {

        route("user/establishments/{establishmentId}/address") {

            /**
             * Add the address field of a establishment
             */
            post {
                val establishmentId = call.parameters["establishmentId"]
                val newAddress = call.receive<NewEstablishmentAddress>()
                try {
                    establishmentId?.let {
                        val updatedEstablishment = establishmentDAO.createAddress(establishmentId.toInt(), newAddress)
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
            patch {
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
        }

    }
}
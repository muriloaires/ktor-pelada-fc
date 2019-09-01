package web

import dao.EstablishmentDAO
import dao.tables.toEstablishment
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
import web.model.incoming.NewEstablishmentBusinessHour
import web.model.outgoing.ErrorResponse

fun Route.establishment(establishmentDAO: EstablishmentDAO) {

    authenticate {

        route("/user/establishments") {

            /**
             * return all establishments from the authenticated user
             */
            get {
                val userId = call.user!!.id
                val establishments = establishmentDAO.getUserEstablishments(userId)
                call.respond(HttpStatusCode.OK, establishments.map { it.toEstablishment() })
            }

            /**
             * Create a establishment to the authenticated user
             */
            post {
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
        }

        route("/user/establishments/{establishmentId}") {
            /**
             * return a specific establishment by its id
             */
            get {
                val establishmentId = call.parameters["establishmentId"]
                establishmentId?.let {
                    establishmentDAO.getEstablishment(it.toInt())?.let { establishmentRow ->
                        call.respond(HttpStatusCode.OK, establishmentRow.toEstablishment())
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("parameter establishmentId is required"))
                }
            }

            /**
             * Update the establishment fields
             */
            patch {
                val establishmentId = call.parameters["establishmentId"]
                establishmentId?.let {
                    val editedEstablishmentBody = call.receive<EditedEstablishment>()
                    val editedEstablishment =
                        establishmentDAO.updateEstablishment(establishmentId.toInt(), editedEstablishmentBody)
                    editedEstablishment?.let {
                        call.respond(HttpStatusCode.OK, editedEstablishment.toEstablishment())
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("parameter establishmentId is required"))
                }
            }


            /**
             * Delete and establishment
             */
            delete {
                val establishmentId = call.parameters["establishmentId"]
                establishmentId?.let {
                    val deleted = establishmentDAO.deleteEstablishment(establishmentId.toInt())
                    call.respond(if (deleted) HttpStatusCode.OK else HttpStatusCode.NotFound)
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("parameter establishmentId is required"))
                }
            }
        }


        patch("/user/establishments/{establishmentId}/businesshours/{dayOfWeek}") {
            val establishmentId = call.parameters["establishmentId"]
            val dayOfWeek = call.parameters["dayOfWeek"]
            establishmentId?.let {
                dayOfWeek?.let {
                    val newBusiness = call.receive<NewEstablishmentBusinessHour>()
                    try {
                        establishmentDAO.updateEstablishmentBusinessHours(
                            establishmentId.toInt(),
                            newBusiness,
                            dayOfWeek.toInt()
                        )?.let {
                            call.respond(HttpStatusCode.OK, it.toEstablishment())
                        } ?: run {
                            call.respond(HttpStatusCode.NotFound, ErrorResponse("Establishment not found"))
                        }
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
                    }

                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("parameter dayOfWeek is required"))
                }

            } ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("parameter establishmentId is required"))
            }
        }

    }

}
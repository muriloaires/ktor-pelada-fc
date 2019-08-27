package web

import dao.EstablishmentCourtsDAO
import dao.tables.toSportCourt
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import util.extensions.toErrorResponse
import web.model.incoming.EditEstablishmentCourt
import web.model.incoming.NewEstablishmentCourt
import web.model.outgoing.ErrorResponse

fun Route.establishmentSportCourt(establishmentCourtsDAO: EstablishmentCourtsDAO) {

    authenticate {
        /**
         * Get all sport courts from a establishment
         */
        get("user/establishments/{establishmentId}/courts") {
            val establishmentId = call.parameters["establishmentId"]
            establishmentId?.let {
                call.respond(
                    HttpStatusCode.OK,
                    establishmentCourtsDAO.getAllEstablishmentCourts(establishmentId.toInt())
                )
            } ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing establishmentId parameter"))
            }
        }

        /**
         * Create a new sport court to a establishment
         */
        post("user/establishments/{establishmentId}/courts") {
            val establishmentId = call.parameters["establishmentId"]
            val newCourt = call.receive<NewEstablishmentCourt>()
            establishmentId?.let {
                try {
                    val sportCourt = establishmentCourtsDAO.createCourt(establishmentId.toInt(), newCourt)
                    sportCourt?.let {
                        call.respond(HttpStatusCode.Created, sportCourt.toSportCourt())
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
                }

            } ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing establishmentId parameter"))
            }
        }

        /**
         * Update a sport court
         */
        patch("user/establishments/{establishmentId}/courts/{courtId}") {
            val establishmentId = call.parameters["establishmentId"]
            val courtId = call.parameters["courtId"]
            establishmentId?.let {
                courtId?.let {
                    val editedCourtBody = call.receive<EditEstablishmentCourt>()
                    val editedCourt =
                        establishmentCourtsDAO.updateCourt(establishmentId.toInt(), courtId.toInt(), editedCourtBody)
                    editedCourt?.let {
                        call.respond(HttpStatusCode.Created, editedCourt.toSportCourt())
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing courtId parameter"))
                }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing establishmentId parameter"))
            }
        }

        /**
         * Remove a sport court
         */
        delete("user/establishments/{establishmentId}/courts/{courtId}") {
            val establishmentId = call.parameters["establishmentId"]
            val courtId = call.parameters["courtId"]
            establishmentId?.let {
                courtId?.let {
                    val deleted = establishmentCourtsDAO.deleteCourt(establishmentId.toInt(), courtId.toInt())
                    call.respond(if (deleted) HttpStatusCode.OK else HttpStatusCode.NotFound)
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing courtId parameter"))
                }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing establishmentId parameter"))
            }
        }
    }


}
package web

import dao.EstablishmentCourtsDAO
import dao.tables.toSportCourt
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.*
import util.extensions.toErrorResponse
import web.model.incoming.EditEstablishmentCourt
import web.model.incoming.NewEstablishmentCourt
import web.model.outgoing.ErrorResponse
import web.request.MultipartHandler

fun Route.establishmentSportCourt(establishmentCourtsDAO: EstablishmentCourtsDAO) {

    authenticate {

        route("user/establishments/{establishmentId}/courts") {

            /**
             * Get all sport courts from a establishment
             */
            get {
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
            post {
                val establishmentId = call.parameters["establishmentId"]
                try {

                    establishmentId?.let {
                        val multipart = call.receiveMultipart()
                        var photoFile: String? = null
                        var newCourtBody: NewEstablishmentCourt? = null

                        multipart.forEachPart { part ->
                            if (part is PartData.FormItem) {
                                newCourtBody = MultipartHandler.getFormItem(part, NewEstablishmentCourt::class.java)
                            } else if (part is PartData.FileItem) {
                                photoFile = MultipartHandler.saveMultipartFile(part)
                            }
                        }

                        photoFile?.let { newCourtBody?.courtPhotoUrl = it }

                        newCourtBody?.let {
                            establishmentCourtsDAO.createCourt(establishmentId.toInt(), it)?.let { sportCourtRow ->
                                call.respond(HttpStatusCode.Created, sportCourtRow.toSportCourt())
                            } ?: run {
                                call.respond(HttpStatusCode.NotFound)
                            }
                        }

                    } ?: run {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("establishmentId parameter is required"))
                    }

                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
                }
            }
        }

        route("user/establishments/courts/{courtId}") {
            /**
             * Update a sport court
             */
            patch {
                val courtId = call.parameters["courtId"]
                courtId?.let {
                    val multiPart = call.receiveMultipart()
                    var photoFile: String? = null
                    var editedCourtBody: EditEstablishmentCourt? = null
                    multiPart.forEachPart { part ->
                        if (part is PartData.FormItem) {
                            editedCourtBody = MultipartHandler.getFormItem(part, EditEstablishmentCourt::class.java)
                        } else if (part is PartData.FileItem) {
                            photoFile = MultipartHandler.saveMultipartFile(part)
                        }
                    }
                    photoFile?.let { editedCourtBody?.courtPhotoUrl = it }
                    editedCourtBody?.let {
                        establishmentCourtsDAO.updateCourt(courtId.toInt(), it)?.let { sportCourtRow ->
                            call.respond(HttpStatusCode.Created, sportCourtRow.toSportCourt())
                        } ?: run {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }

                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("courtId parameter is required"))
                }
            }

            /**
             * Remove a sport court
             */
            delete {
                val courtId = call.parameters["courtId"]
                courtId?.let {
                    val deleted = establishmentCourtsDAO.deleteCourt(courtId.toInt())
                    call.respond(if (deleted) HttpStatusCode.OK else HttpStatusCode.NotFound)
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing courtId parameter"))
                }
            }
        }

    }


}
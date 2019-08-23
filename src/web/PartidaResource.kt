package web

import dao.model.NewPartida
import service.PartidaService
import util.user
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.partida(partidaService: PartidaService) {

    authenticate {
        route("/partida") {

            get {
                val user = call.user!!
                call.respond(user)
            }

            get("/all") {
                call.respond(partidaService.getAllPartidas())
            }

            get("/{id}") {
                call.parameters["id"]?.let {
                    partidaService.getPartida(it.toInt())?.let { partida ->
                        call.respond(partida)
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }

            post("/") {
                val partida = call.receive<NewPartida>()
                call.respond(HttpStatusCode.Created, partidaService.addPartida(partida))
            }


            put("/") {
                val partida = call.receive<NewPartida>()
                val updatedPartida = partidaService.updatePartida(partida)
                updatedPartida?.let {
                    call.respond(HttpStatusCode.OK, updatedPartida)
                } ?: run {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            delete("/{id}") {
                call.parameters["id"]?.let {
                    val removed = partidaService.deletePartida(it.toInt())
                    if (removed) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

            }
        }
    }

}
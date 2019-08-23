package web

import config.JwtConfig
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import model.NewUser
import service.UserSource
import util.user


fun Route.user(userSource: UserSource) {

    post("/register") {

        val newUser = call.receive<NewUser>()
        when {
            userSource.findByEmail(newUser.email) != null ->
                call.respond(HttpStatusCode.BadRequest, "Email ja cadastrado")
            userSource.findByUsername(newUser.username) != null ->
                call.respond(HttpStatusCode.BadRequest, "Username já cadastrado")
            else ->
                call.respond(
                    HttpStatusCode.Created,
                    userSource.addUser(newUser).apply { this.token = JwtConfig.makeToken(this) })
        }

    }

    get("/login") {
        val usernameOrEmail = call.request.queryParameters["usernameOrEmail"]
        val password = call.request.queryParameters["password"]
        val loginType = call.request.queryParameters["loginType"]
        if (usernameOrEmail.isNullOrEmpty() || loginType.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val user = userSource.findUserByLoginRequest(usernameOrEmail, loginType, password!!)
            user?.let {
                val token = JwtConfig.makeToken(it)
                call.respond(HttpStatusCode.OK, it.apply { this.token = token })
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Wrong user or password")
            }
        }
    }


    authenticate {

        /**
         * Rota para update de username
         */
        patch("/users/username") {
            call.user?.let {
                val newUser = call.receive<NewUser>()
                if (newUser.username == it.username || userSource.findByUsername(newUser.username) != null) {
                    call.respond(HttpStatusCode.BadRequest, "Username já cadastrado")
                } else {
                    call.respond(HttpStatusCode.OK, userSource.updateUsername(it.id.value, newUser.username)!!)
                }
            }
        }

        /**
         * Rota para update de email
         */
        patch("/users/email") {
            call.user?.let {
                val newUser = call.receive<NewUser>()
                if (newUser.email == it.email || userSource.findByEmail(newUser.email) != null) {
                    call.respond(HttpStatusCode.BadRequest, "Email já cadastrado")
                } else {
                    call.respond(HttpStatusCode.OK, userSource.updateEmail(it.id.value, newUser.email)!!)
                }
            }
        }
    }


}
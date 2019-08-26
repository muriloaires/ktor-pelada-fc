package web

import config.JwtConfig
import dao.UserDAO
import dao.model.LoginType
import dao.model.toUser
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import util.extensions.toErrorResponse
import util.user
import web.model.incoming.NewUser
import web.model.outgoing.ErrorResponse


fun Route.user(userSource: UserDAO) {

    post("/register") {

        val newUser = call.receive<NewUser>()
        try {
            when {
                userSource.findByEmail(newUser.email) != null ->
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Email ja cadastrado"))
                userSource.findByUsername(newUser.username) != null ->
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Username já cadastrado"))
                else ->
                    call.respond(
                        HttpStatusCode.Created,
                        userSource.addUser(newUser).toUser().apply { this.token = JwtConfig.makeToken(this) })
            }
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
        }


    }

    get("/login") {
        val usernameOrEmail = call.request.queryParameters["usernameOrEmail"]
        val password = call.request.queryParameters["password"]
        val loginType = call.request.queryParameters["loginType"]
        if (usernameOrEmail.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("username or email is required"))
        } else if (loginType.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("loginType is required"))
        } else if (password.isNullOrEmpty() && loginType == LoginType.DEFAULT.value) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("password is required for default loginType"))
        } else {
            val user = userSource.findUserByLoginRequest(usernameOrEmail, loginType, password!!)
            user?.let {
                val token = JwtConfig.makeToken(it.toUser())
                call.respond(HttpStatusCode.OK, it.toUser().apply { this.token = token })
            } ?: run {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Wrong user or password"))
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
                try {
                    if (newUser.username == it.username || userSource.findByUsername(newUser.username) != null) {
                        call.respond(HttpStatusCode.BadRequest, "Username já cadastrado")
                    } else {
                        call.respond(HttpStatusCode.OK, userSource.updateUsername(it.id, newUser.username)!!)
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadGateway, e.toErrorResponse())
                }
            }
        }

        /**
         * Rota para update de email
         */
        patch("/users/email") {
            call.user?.let {
                try {
                    val newUser = call.receive<NewUser>()
                    if (newUser.email == it.email || userSource.findByEmail(newUser.email) != null) {
                        call.respond(HttpStatusCode.BadRequest, "Email já cadastrado")
                    } else {
                        call.respond(HttpStatusCode.OK, userSource.updateEmail(it.id, newUser.email)!!)
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
                }
            }
        }

    }



}

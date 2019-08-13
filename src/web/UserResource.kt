package web

import config.JwtConfig
import de.nielsfalk.ktor.swagger.*
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import model.NewUser
import service.UserSource

@Group("Authentication")
@Location("/register")
class RegisterUser

@Group("Authentication")
@Location("/login")
class Login(val email: String, val password: String)


fun Route.user(userSource: UserSource) {

//    post<RegisterUser, Any>("all".responds(ok<String>(example("model", Any())))) {
//        val newUser = call.receive<NewUser>()
//        userSource.addUser(newUser)?.let {
//            val token = JwtConfig.makeToken(it)
//            call.respond(HttpStatusCode.Created, it.apply { this.token = token })
//        } ?: run {
//            call.respond(HttpStatusCode.BadRequest, "Email ja cadastrado")
//        }
//    }
    get<Login>("Login".responds(ok<String>())) {
        val email = call.request.queryParameters["email"]
        val password = call.request.queryParameters["password"]
        if (email.isNullOrEmpty() or password.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val user = userSource.findUserByCredentials(email!!, password!!)
            user?.let {
                val token = JwtConfig.makeToken(it)
                call.respond(HttpStatusCode.OK, it.apply { this.token = token })
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Wrong user or password")
            }
        }

    }
}
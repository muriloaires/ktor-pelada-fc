package web

import config.JwtConfig
import de.nielsfalk.ktor.swagger.*
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.post
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
class Login(val username: String?, val email: String?, val password: String?, val loginType: String?)


fun Route.user(userSource: UserSource) {

    post<RegisterUser> {
        val newUser = call.receive<NewUser>()

        when {
            userSource.findByEmail(newUser.email) != null ->
                call.respond(HttpStatusCode.BadRequest, "Email ja cadastrado")
            userSource.findByUsername(newUser.username) != null ->
                call.respond(HttpStatusCode.BadRequest, "Username já cadastrado")
            else ->
                call.respond(HttpStatusCode.Created, userSource.addUser(newUser))
        }

    }

    get<Login>("Login".responds(ok<String>())) { login ->
        if (login.username.isNullOrEmpty() or login.loginType.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val user = userSource.findUserByLogin(login)
            user?.let {
                val token = JwtConfig.makeToken(it)
                call.respond(HttpStatusCode.OK, it.apply { this.token = token })
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Wrong user or password")
            }
        }

    }
}
package web

import config.JwtConfig
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import model.NewUser
import service.UserSource

@Location("/register")
class RegisterUserRequest

@Location("/login")
class LoginRequest(val usernameOrEmail: String, val password: String?, val loginType: String)


fun Route.user(userSource: UserSource) {

    post<RegisterUserRequest> {
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

    get<LoginRequest> { loginRequest ->
        if (loginRequest.usernameOrEmail.isNullOrEmpty() or loginRequest.loginType.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val user = userSource.findUserByLoginRequest(loginRequest)
            user?.let {
                val token = JwtConfig.makeToken(it)
                call.respond(HttpStatusCode.OK, it.apply { this.token = token })
            } ?: run {
                call.respond(HttpStatusCode.NotFound, "Wrong user or password")
            }
        }

    }
}
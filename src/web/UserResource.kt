package web
import config.JwtConfig
import model.NewUser
import service.UserSource
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post

fun Route.user(userSource: UserSource) {
    post("/register") {
        val newUser = call.receive<NewUser>()
        userSource.addUser(newUser)?.let {
            val token = JwtConfig.makeToken(it)
            call.respond(HttpStatusCode.Created, it.apply { this.token = token })
        } ?: run {
            call.respond(HttpStatusCode.BadRequest, "Email ja cadastrado")
        }

    }

    get("/login") {
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
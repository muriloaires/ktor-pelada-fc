import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import service.DatabaseFactory
import service.PartidaService
import service.UserService
import service.UserSource
import web.partida
import web.user
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun Application.module() {

    val issuer = "https://jwt-provider-domain/"
    val realm = "ktor sample app"

    install(DefaultHeaders)
    install(CallLogging)

    val userSource: UserSource = UserService()
    install(Authentication) {
        val jwtVerifier = makeJwtVerifier(issuer)
        jwt {
            verifier(jwtVerifier)
            this.realm = realm
            validate {
                it.payload.getClaim("id").asInt()?.let(userSource::findUserById)
            }
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(Routing) {
        partida(PartidaService())
        user(userSource)
    }

    DatabaseFactory.init()

}

fun main() {
    embeddedServer(Netty, 8080, module = Application::module).start()
}

private val algorithm = Algorithm.HMAC512("zAP5MBA4B4Ijz0MZaS48")
private fun makeJwtVerifier(issuer: String): JWTVerifier = JWT
    .require(algorithm)
    .withIssuer(issuer)
    .build()
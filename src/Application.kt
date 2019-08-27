import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import dao.EstablishmentDAO
import dao.UserDAO
import dao.factory.DatabaseFactory
import dao.model.toUser
import dao.services.EstablishmentServiceDAO
import dao.services.UserServiceDAO
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import web.establishment
import web.establishmentAddress
import web.user


fun Application.module() {
    val userSource: UserDAO = UserServiceDAO()
    val establishmentDAO: EstablishmentDAO = EstablishmentServiceDAO()
    val issuer = "https://jwt-provider-domain/"
    val realm = "ktor sample app"

    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(Authentication) {
        val jwtVerifier = makeJwtVerifier(issuer)
        jwt {
            verifier(jwtVerifier)
            this.realm = realm
            validate {
                it.payload.getClaim("id").asInt()?.let{id ->
                    userSource.findById(id)?.toUser()
                }
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
            setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        }
    }
    routing {
        user(userSource)
        establishment(establishmentDAO)
        establishmentAddress(establishmentDAO)
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
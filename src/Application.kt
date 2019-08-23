import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import de.nielsfalk.ktor.swagger.SwaggerSupport
import de.nielsfalk.ktor.swagger.version.shared.Contact
import de.nielsfalk.ktor.swagger.version.shared.Information
import de.nielsfalk.ktor.swagger.version.v2.Swagger
import de.nielsfalk.ktor.swagger.version.v3.OpenApi
import web.partida
import web.user
import io.ktor.application.Application
import io.ktor.application.call
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
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import model.Establishment
import org.jetbrains.exposed.sql.transactions.transaction
import service.*


fun Application.module() {
    val userSource: UserSource = UserService()
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
    install(SwaggerSupport) {
        forwardRoot = true
        val information = Information(
            version = "0.1",
            title = "sample api implemented in ktor",
            description = "This is a sample which combines [ktor](https://github.com/Kotlin/ktor) with [swaggerUi](https://swagger.io/). You find the sources on [github](https://github.com/nielsfalk/ktor-swagger)",
            contact = Contact(
                name = "Niels Falk",
                url = "https://nielsfalk.de"
            )
        )
        swagger = Swagger().apply {
            info = information
        }
        openApi = OpenApi().apply {
            info = information
        }
    }
    routing {
        get("/teste") {
            val list = transaction {
                Establishment.all().toList()
            }
            call.respond(list)
        }
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
package web

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respondFile
import io.ktor.routing.Route
import io.ktor.routing.get
import java.io.File

fun Route.image(){
    authenticate {
        /**
         * Load a image by its name
         */
        get("/image/{imageName}") {
            val imageName = call.parameters["imageName"]
            call.respondFile(File("C:\\ktor\\uploads\\$imageName"))
        }
    }
}
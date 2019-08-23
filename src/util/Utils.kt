package util

import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import model.User

val ApplicationCall.user get() = authentication.principal<User>()

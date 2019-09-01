package util.extensions

import org.jetbrains.exposed.exceptions.ExposedSQLException
import web.model.outgoing.ErrorResponse

fun IllegalArgumentException.toErrorResponse() = ErrorResponse(localizedMessage)

fun ExposedSQLException.checkIfIllegalArgument(): Exception {
    return when {
        this.localizedMessage.contains("duplicate key value violates unique constraint \"users_username_unique\"") -> IllegalArgumentException("username já cadastrado")
        this.localizedMessage.contains("duplicate key value violates unique constraint \"users_email_unique\"") -> throw IllegalArgumentException("email já cadastrado")
        else -> this
    }
}
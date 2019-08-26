package util.extensions

import web.model.outgoing.ErrorResponse

fun IllegalArgumentException.toErrorResponse() = ErrorResponse(localizedMessage)
package web

import config.JwtConfig
import dao.UserDAO
import dao.tables.LoginType
import dao.tables.toUser
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import model.User
import util.extensions.toErrorResponse
import util.user
import web.model.incoming.EditedUser
import web.model.incoming.NewUser
import web.model.outgoing.ErrorResponse
import web.request.MultipartHandler

fun Route.user(userSource: UserDAO) {

    post("/register") {
        var user: User? = null
        try {
            val multiPart = call.receiveMultipart()
            var photoFile: String? = null

            multiPart.forEachPart { part ->
                if (part is PartData.FormItem) {
                    user = userSource.addUser(MultipartHandler.getFormItem(part, NewUser::class.java)).toUser()
                } else if (part is PartData.FileItem) {
                    photoFile = MultipartHandler.saveMultipartFile(part)
                }
                part.dispose
            }
            call.respond(HttpStatusCode.Created,
                user!!.apply {
                    this.token = JwtConfig.makeToken(this)
                    this.photoUrl = photoFile
                }
            )
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
        }
    }

    get("/login") {
        val usernameOrEmail = call.request.queryParameters["usernameOrEmail"]
        val password = call.request.queryParameters["password"]
        val loginType = call.request.queryParameters["loginType"]
        if (usernameOrEmail.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("username or email is required"))
        } else if (loginType.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("loginType is required"))
        } else if (password.isNullOrEmpty() && loginType == LoginType.DEFAULT.value) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("password is required for default loginType"))
        } else {
            val user = userSource.findUserByLoginRequest(usernameOrEmail, loginType, password!!)
            user?.let {
                val token = JwtConfig.makeToken(it.toUser())
                call.respond(HttpStatusCode.OK, it.toUser().apply { this.token = token })
            } ?: run {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Wrong user or password"))
            }
        }
    }


    authenticate {

        patch("/users") {
            val multiPart = call.receiveMultipart()
            var photoFile: String? = null
            var editedUserBody: EditedUser? = null
            try {
                multiPart.forEachPart { part ->
                    if (part is PartData.FormItem) {
                        editedUserBody = MultipartHandler.getFormItem(part, EditedUser::class.java)
                    } else if (part is PartData.FileItem) {
                        photoFile = MultipartHandler.saveMultipartFile(part)
                    }
                    part.dispose
                }
                editedUserBody?.let { editedUser ->
                    photoFile?.let { editedUser.photoUrl = it }
                    userSource.updateUser(call.user!!.id, editedUser).toUser().apply {
                        call.respond(HttpStatusCode.OK, this)
                    }
                } ?: run {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Body not found"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.toErrorResponse())
            }
        }

    }

}


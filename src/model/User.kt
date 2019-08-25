package model

import io.ktor.auth.Principal

data class User(
    val id : Int,
    val name: String,
    val username: String,
    val email: String,
    val loginType: String,
    val isAdvertiser: Boolean,
    val establishments : List<Establishment>,
    var token: String? = null
) : Principal
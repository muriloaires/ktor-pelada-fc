package service

import model.NewUser
import model.User
import web.Login

interface UserSource {

    suspend fun getAllUsers(): List<User>

    suspend fun getUser(id: Int): User?

    fun findUserById(id: Int): User?

    suspend fun findByEmail(email: String): User?

    suspend fun findUserByCredentials(username: String, password: String): User?

    suspend fun findUserByLogin(login: Login): User?

    suspend fun updateUser(user: NewUser): User?

    suspend fun addUser(user: NewUser): User

    suspend fun deleteUser(id: Int): Boolean

    suspend fun findByUsername(username: String): User?

}
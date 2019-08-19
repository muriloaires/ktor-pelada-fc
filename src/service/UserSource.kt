package service

import model.NewUser
import model.User

interface UserSource {

    suspend fun getAllUsers(): List<User>

    suspend fun getUser(id: Int): User?

    fun findUserById(id: Int): User?

    suspend fun findByEmail(email: String): User?

    suspend fun findUserByCredentials(usernameOrEmail: String, password: String): User?

    suspend fun findUserByLoginRequest(usernameOrEmail: String, loginType: String, password: String): User?

    suspend fun findUserBySocialNetwork(email: String, loginType: String): User?

    suspend fun updateUsername(userId: Int, newUsername: String): User

    suspend fun updateEmail(userId: Int, newEmail: String): User

    suspend fun updateUser(user: NewUser): User?

    suspend fun addUser(user: NewUser): User

    suspend fun deleteUser(id: Int): Boolean

    suspend fun findByUsername(username: String): User?

}
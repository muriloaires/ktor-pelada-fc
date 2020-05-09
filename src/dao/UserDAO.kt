package dao

import dao.tables.UserRow
import web.model.incoming.EditedUser
import web.model.incoming.NewUser

interface UserDAO {

    suspend fun getAllUsers(): List<UserRow>

    suspend fun getUser(id: Int): UserRow?

    fun findUserById(id: Int): UserRow?

    suspend fun findById(id: Int): UserRow?

    suspend fun findByEmail(email: String): UserRow?

    suspend fun findUserByCredentials(usernameOrEmail: String, password: String): UserRow?

    suspend fun findUserByLoginRequest(usernameOrEmail: String, loginType: String, password: String): UserRow?

    suspend fun findUserBySocialNetwork(email: String, loginType: String): UserRow?

    suspend fun addUser(user: NewUser): UserRow

    suspend fun deleteUser(id: Int): Boolean

    suspend fun findByUsername(username: String): UserRow?

    suspend fun updateUser(userId: Int, editedUser: EditedUser): UserRow

}
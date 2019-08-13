package service
import model.NewUser
import model.User

interface UserSource {

    suspend fun getAllUsers(): List<User>

    suspend fun getUser(id: Int): User?

    fun findUserById(id: Int): User?

    suspend fun findByEmail(email : String) : User?

    suspend fun findUserByCredentials(email: String, password: String): User?

    suspend fun updateUser(user: NewUser): User?

    suspend fun addUser(user: NewUser): User?

    suspend fun deleteUser(id: Int): Boolean

}
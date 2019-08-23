package service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.LoginType
import model.NewUser
import model.User
import model.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import security.Hash
import util.isEmail

class UserService : UserSource {

    override fun findUserById(id: Int): User? {
        return User.findById(id)
    }

    override suspend fun findUserByCredentials(usernameOrEmail: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            val hashPass = Hash.sha256(password)
            User.find {
                if (isEmail(usernameOrEmail)) {
                    Users.password.eq(hashPass) and Users.email.eq(usernameOrEmail)
                } else {
                    Users.password.eq(hashPass) and Users.username.eq(usernameOrEmail)
                }
            }.singleOrNull()
        }
    }

    //    return withContext(Dispatchers.IO) {}
    override suspend fun findUserBySocialNetwork(email: String, loginType: String): User? {
        return withContext(Dispatchers.IO) {
            User.find {
                Users.email.eq(email) and Users.loginType.eq(loginType)
            }.singleOrNull()
        }
    }

    override suspend fun findUserByLoginRequest(usernameOrEmail: String, loginType: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            when (loginType) {
                LoginType.DEFAULT.value -> findUserByCredentials(usernameOrEmail, password)
                else -> findUserBySocialNetwork(usernameOrEmail, loginType)
            }
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) { User.all().toList() }
    }

    override suspend fun getUser(id: Int): User? {
        return withContext(Dispatchers.IO) {
            User.findById(id)
        }
    }

    override suspend fun updateUser(user: NewUser): User? {
        return withContext(Dispatchers.IO) {
            val id = user.id
            id?.let {
                DatabaseFactory.dbQuery {
                    Users.update({ Users.id eq id }) {
                        it[name] = user.name
                        it[email] = user.email
                    }
                }
                getUser(id)
            } ?: run {
                addUser(user)
            }
        }
    }

    override suspend fun updateUsername(userId: Int, newUsername: String): User? {
        return withContext(Dispatchers.IO) {
            User.findById(userId)?.apply {
                username = newUsername
            }
        }
    }

    override suspend fun updateEmail(userId: Int, newEmail: String): User? {
        return withContext(Dispatchers.IO) {
            User.findById(userId)?.apply {
                email = newEmail
            }
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return User.find { Users.email eq email }.singleOrNull()
    }

    override suspend fun findByUsername(username: String): User? {
        return User.find { Users.username eq username }.singleOrNull()
    }

    override suspend fun addUser(user: NewUser): User {
        return User.new {
            name = user.name
            email = user.email
            username = user.username
            password = user.password?.let { password ->
                Hash.sha256(password)
            } ?: run {
                Hash.sha256(System.currentTimeMillis().toString())
            }
            loginType = user.loginType
        }
    }

    override suspend fun deleteUser(id: Int): Boolean {
        User.findById(id)?.let {
            it.delete()
            return true
        } ?: run {
            return false
        }
    }

}
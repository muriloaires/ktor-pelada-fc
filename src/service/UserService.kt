package service

import dao.model.*
import model.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import security.Hash
import util.isEmail

class UserService : UserSource {

    override fun findUserById(id: Int): User? {
        return transaction { UserRow.findById(id)?.toUser() }
    }

    override suspend fun findUserByCredentials(usernameOrEmail: String, password: String): User? {
        return DatabaseFactory.dbQuery {
            val hashPass = Hash.sha256(password)
            UserRow.find {
                if (isEmail(usernameOrEmail)) {
                    Users.password.eq(hashPass) and Users.email.eq(usernameOrEmail)
                } else {
                    Users.password.eq(hashPass) and Users.username.eq(usernameOrEmail)
                }
            }.singleOrNull()
        }?.toUser()
    }

    override suspend fun findUserBySocialNetwork(email: String, loginType: String): User? {
        return DatabaseFactory.dbQuery {
            UserRow.find {
                Users.email.eq(email) and Users.loginType.eq(loginType)
            }.singleOrNull()
        }?.toUser()
    }

    override suspend fun findUserByLoginRequest(
        usernameOrEmail: String,
        loginType: String,
        password: String
    ): User? {
        return when (loginType) {
            LoginType.DEFAULT.value -> findUserByCredentials(usernameOrEmail, password)
            else -> findUserBySocialNetwork(usernameOrEmail, loginType)
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return DatabaseFactory.dbQuery { UserRow.all().map { it.toUser() }.toList() }
    }

    override suspend fun getUser(id: Int): User? {
        return DatabaseFactory.dbQuery {
            UserRow.findById(id)
        }?.toUser()
    }


    override suspend fun updateUsername(userId: Int, newUsername: String): User? {
        return DatabaseFactory.dbQuery {
            UserRow.findById(userId)?.apply {
                username = newUsername
            }
        }?.toUser()
    }

    override suspend fun updateEmail(userId: Int, newEmail: String): User? {
        return DatabaseFactory.dbQuery {
            UserRow.findById(userId)?.apply {
                email = newEmail
            }
        }?.toUser()
    }

    override suspend fun findByEmail(email: String): User? {
        return UserRow.find { Users.email eq email }.singleOrNull()?.toUser()
    }

    override suspend fun findByUsername(username: String): User? {
        return UserRow.find { Users.username eq username }.singleOrNull()?.toUser()
    }

    override suspend fun addUser(user: NewUser): User {
        return UserRow.new {
            name = user.name
            email = user.email
            username = user.username
            password = user.password?.let { password ->
                Hash.sha256(password)
            } ?: run {
                Hash.sha256(System.currentTimeMillis().toString())
            }
            loginType = user.loginType
        }.toUser()
    }

    override suspend fun deleteUser(id: Int): Boolean {
        UserRow.findById(id)?.let {
            it.delete()
            return true
        } ?: run {
            return false
        }
    }


}
package dao.services

import dao.UserDAO
import dao.factory.DatabaseFactory
import dao.tables.LoginType
import dao.tables.UserRow
import dao.tables.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import security.Hash
import util.isEmail
import web.model.incoming.EditedUser
import web.model.incoming.NewUser

class UserServiceDAO : UserDAO {

    override fun findUserById(id: Int): UserRow? {
        return transaction { UserRow.findById(id) }
    }

    override suspend fun findById(id: Int): UserRow? {
        return DatabaseFactory.dbQuery {
            UserRow.findById(id)
        }
    }

    override suspend fun findUserByCredentials(usernameOrEmail: String, password: String): UserRow? {
        return DatabaseFactory.dbQuery {
            val hashPass = Hash.sha256(password)
            UserRow.find {
                if (isEmail(usernameOrEmail)) {
                    Users.password.eq(hashPass) and Users.email.eq(usernameOrEmail)
                } else {
                    Users.password.eq(hashPass) and Users.username.eq(usernameOrEmail)
                }
            }.singleOrNull()
        }
    }

    override suspend fun findUserBySocialNetwork(email: String, loginType: String): UserRow? {
        return DatabaseFactory.dbQuery {
            UserRow.find {
                Users.email.eq(email) and Users.loginType.eq(loginType)
            }.singleOrNull()
        }
    }

    override suspend fun findUserByLoginRequest(
        usernameOrEmail: String,
        loginType: String,
        password: String
    ): UserRow? {
        return when (loginType) {
            LoginType.DEFAULT.value -> findUserByCredentials(usernameOrEmail, password)
            else -> findUserBySocialNetwork(usernameOrEmail, loginType)
        }
    }

    override suspend fun getAllUsers(): List<UserRow> {
        return DatabaseFactory.dbQuery { UserRow.all().toList() }
    }

    override suspend fun getUser(id: Int): UserRow? {
        return DatabaseFactory.dbQuery {
            UserRow.findById(id)
        }
    }


    override suspend fun findByEmail(email: String): UserRow? {
        return DatabaseFactory.dbQuery {
            UserRow.find { Users.email eq email }.singleOrNull()
        }
    }

    override suspend fun findByUsername(username: String): UserRow? {
        return DatabaseFactory.dbQuery { UserRow.find { Users.username eq username }.singleOrNull() }
    }

    override suspend fun addUser(user: NewUser): UserRow {
        return DatabaseFactory.dbQuery {
            UserRow.new {
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
    }

    override suspend fun updateUser(userId: Int, editedUser: EditedUser): UserRow {
        return DatabaseFactory.dbQuery {
            UserRow.findById(userId)!!.apply {
                var changed = false
                if (editedUser.email.isNullOrEmpty().not() && this.loginType != LoginType.DEFAULT.value) {
                    throw IllegalArgumentException("email cannot be changed for default loginType")
                }
                editedUser.name?.let {
                    this.name = it
                    changed = true
                }
                editedUser.username?.let {
                    this.username = it
                    changed = true
                }
                editedUser.email?.let {
                    this.email = it
                    changed = true
                }
                editedUser.photoUrl?.let {
                    this.photoUrl = it
                    changed = true
                }
                if (changed) {
                    this.updatedAt = DateTime.now()
                }
            }
        }
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
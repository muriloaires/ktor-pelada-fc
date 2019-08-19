package service

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
        return transaction {
            Users.select {
                (Users.id eq id)
            }.mapNotNull { toUser(it) }.singleOrNull()
        }
    }

    override suspend fun findUserByCredentials(usernameOrEmail: String, password: String): User? {
        return DatabaseFactory.dbQuery {
            val hashPass = Hash.sha256(password)
            Users.select {
                if (isEmail(usernameOrEmail)) {
                    Users.password.eq(hashPass) and Users.email.eq(usernameOrEmail)
                } else {
                    Users.password.eq(hashPass) and Users.username.eq(usernameOrEmail)
                }
            }.mapNotNull {
                toUser(it)
            }.singleOrNull()
        }
    }

    override suspend fun findUserBySocialNetwork(email: String, loginType: String): User? = DatabaseFactory.dbQuery {
        Users.select {
            Users.email.eq(email) and Users.loginType.eq(loginType)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    override suspend fun findUserByLoginRequest(usernameOrEmail: String, loginType: String, password: String): User? {
        return when (loginType) {
            LoginType.DEFAULT.value -> findUserByCredentials(usernameOrEmail, password)
            else -> findUserBySocialNetwork(usernameOrEmail, loginType)
        }
    }

    override suspend fun getAllUsers(): List<User> = DatabaseFactory.dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    override suspend fun getUser(id: Int): User? = DatabaseFactory.dbQuery {
        Users.select {
            (Users.id eq id)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    override suspend fun updateUser(user: NewUser): User? {
        val id = user.id
        id?.let {
            DatabaseFactory.dbQuery {
                Users.update({ Users.id eq id }) {
                    it[name] = user.name
                    it[email] = user.email
                }
            }
            return getUser(id)
        } ?: run {
            return addUser(user)
        }
    }

    override suspend fun updateUsername(userId: Int, newUsername: String): User {
        return getUser(
            DatabaseFactory.dbQuery {
                Users.update({ Users.id eq userId }) {
                    it[username] = newUsername
                }
            }
        )!!
    }

    override suspend fun updateEmail(userId: Int, newEmail: String): User {
        return getUser(
            DatabaseFactory.dbQuery {
                Users.update({ Users.id eq userId }) {
                    it[email] = newEmail
                }
            }
        )!!
    }

    override suspend fun findByEmail(email: String): User? = DatabaseFactory.dbQuery {
        Users.select {
            (Users.email eq email)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    override suspend fun findByUsername(username: String): User? = DatabaseFactory.dbQuery {
        Users.select {
            (Users.username eq username)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    override suspend fun addUser(user: NewUser): User {
        var key = 0
        DatabaseFactory.dbQuery {
            key = (Users.insert {
                it[name] = user.name
                it[email] = user.email
                it[username] = user.username
                it[password] = user.password?.let { password ->
                    Hash.sha256(password)
                } ?: run {
                    Hash.sha256(System.currentTimeMillis().toString())
                }
                it[loginType] = user.loginType
            } get Users.id)
        }
        return getUser(key)!!
    }

    override suspend fun deleteUser(id: Int) = DatabaseFactory.dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            name = row[Users.name],
            username = row[Users.username],
            email = row[Users.email],
            isAdvertiser = row[Users.isAdvertiser],
            token = null,
            loginType = row[Users.loginType]
        )
}
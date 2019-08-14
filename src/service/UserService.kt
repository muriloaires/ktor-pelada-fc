package service

import model.LoginType
import model.NewUser
import model.User
import model.Users
import security.Hash
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import web.Login

class UserService : UserSource {

    override fun findUserById(id: Int): User? {
        return transaction {
            Users.select {
                (Users.id eq id)
            }.mapNotNull { toUser(it) }.singleOrNull()
        }
    }

    override suspend fun findUserByCredentials(username: String, password: String): User? {
        return DatabaseFactory.dbQuery {
            val hashPass = Hash.sha256(password)
            Users.select {
                Users.password.eq(hashPass) and Users.username.eq(username)
            }.mapNotNull {
                toUser(it)
            }.singleOrNull()
        }
    }

    override suspend fun findUserByLogin(login: Login): User? {
        login.loginType?.let {
            return when (it) {
                LoginType.DEFAULT.value -> findUserByCredentials(login.username!!, login.password!!)
                else -> findByUsername(login.username!!)
            }
        } ?: run { return null }
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
            token = "",
            loginType = row[Users.loginType]
        )
}
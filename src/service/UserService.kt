package service

import model.NewUser
import model.User
import model.Users
import security.Hash
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserService : UserSource {

    override fun findUserById(id: Int): User? {
        return transaction {
            Users.select {
                (Users.id eq id)
            }.mapNotNull { toUser(it) }.singleOrNull()
        }
    }

    override suspend fun findUserByCredentials(email: String, password: String): User? {
        return DatabaseFactory.dbQuery {
            val hashPass = Hash.sha256(password)
            Users.select {
                Users.password.eq(hashPass) and Users.email.eq(email)
            }.mapNotNull {
                toUser(it)
            }.singleOrNull()
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

    override suspend fun findByEmail(email: String): User? = DatabaseFactory.dbQuery {
        Users.select {
            (Users.email eq email)
        }.mapNotNull { toUser(it) }.singleOrNull()
    }

    override suspend fun addUser(user: NewUser): User? {
        findByEmail(user.email)?.let {
            return null
        } ?: run {
            var key = 0
            DatabaseFactory.dbQuery {
                key = (Users.insert {
                    it[name] = user.name
                    it[email] = user.email
                    it[password] = Hash.sha256(user.password)
                } get Users.id)
            }
            return getUser(key)!!
        }
    }

    override suspend fun deleteUser(id: Int) = DatabaseFactory.dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            password = row[Users.password],
            token = ""
        )
}
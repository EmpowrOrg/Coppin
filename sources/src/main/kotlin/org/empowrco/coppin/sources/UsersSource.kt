package org.empowrco.coppin.sources

import org.empowrco.coppin.db.Users
import org.empowrco.coppin.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface UsersSource {
    suspend fun getUser(id: UUID): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUser(id: UUID): Boolean
    suspend fun getUsers(): List<User>
}

internal class RealUsersSource : UsersSource {
    override suspend fun getUser(id: UUID): User? = dbQuery {
        Users.select { Users.id eq id }.limit(1).firstNotNullOfOrNull { it.toUser() }
    }

    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map { it.toUser() }
    }

    override suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select { Users.email eq email }.limit(1).firstNotNullOfOrNull { it.toUser() }
    }

    override suspend fun createUser(user: User) = dbQuery {
        Users.insert {
            insert(it, user, true)
        }
        Unit
    }

    private fun insert(
        it: UpdateBuilder<*>,
        user: User,
        isCreate: Boolean,
    ) {
        if (isCreate) {
            it[Users.id] = user.id
            it[Users.createdAt] = user.createdAt
        }
        it[Users.email] = user.email
        it[Users.type] = user.type
        it[Users.firstName] = user.firstName
        it[Users.lastName] = user.lastName
        it[Users.isAuthorized] = user.isAuthorized
        it[Users.passwordHash] = user.passwordHash
    }

    override suspend fun updateUser(user: User): Boolean = dbQuery {
        Users.update {
            insert(it, user, false)
        } > 0
    }

    override suspend fun deleteUser(id: UUID): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun ResultRow.toUser(): User {
        return User(
            id = this[Users.id].value,
            firstName = this[Users.firstName],
            lastName = this[Users.lastName],
            email = this[Users.email],
            passwordHash = this[Users.passwordHash],
            type = this[Users.type],
            isAuthorized = this[Users.isAuthorized],
            createdAt = this[Users.createdAt],
            lastModifiedAt = this[Users.lastModifiedAt]
        )
    }
}

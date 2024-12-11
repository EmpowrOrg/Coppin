package org.empowrco.coppin.sources

import org.empowrco.coppin.db.UserAccessKeys
import org.empowrco.coppin.db.Users
import org.empowrco.coppin.models.User
import org.empowrco.coppin.models.UserAccessKey
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
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
    suspend fun deleteKey(id: UUID): Boolean
    suspend fun createKey(key: UserAccessKey)
    suspend fun getKey(id: UUID): UserAccessKey?
    suspend fun getKeyByValue(value: String): UserAccessKey?
    suspend fun getKeysForUser(userId: UUID): List<UserAccessKey>
}

internal class RealUsersSource : UsersSource {
    override suspend fun getUser(id: UUID): User? = dbQuery {
        Users.selectAll().where { Users.id eq id }.limit(1).firstNotNullOfOrNull { it.toUser() }
    }

    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map { it.toUser() }
    }

    override suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.selectAll().where { Users.email eq email }.limit(1).firstNotNullOfOrNull { it.toUser() }
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
        it[Users.lastModifiedAt] = user.lastModifiedAt
    }

    override suspend fun updateUser(user: User): Boolean = dbQuery {
        Users.update({ Users.id eq user.id }) {
            insert(it, user, false)
        } > 0
    }

    override suspend fun deleteKey(id: UUID): Boolean = dbQuery {
        UserAccessKeys.deleteWhere { UserAccessKeys.id eq id } > 0
    }

    override suspend fun createKey(key: UserAccessKey) = dbQuery {
        UserAccessKeys.insert {
            it[UserAccessKeys.id] = key.id
            it[user] = key.userId
            it[UserAccessKeys.name] = key.name
            it[UserAccessKeys.type] = key.type
            it[UserAccessKeys.key] = key.key
            it[UserAccessKeys.createdAt] = key.createdAt
            it[UserAccessKeys.lastModifiedAt] = key.lastModifiedAt
        }
        Unit
    }

    override suspend fun deleteUser(id: UUID): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    override suspend fun getKey(id: UUID): UserAccessKey? = dbQuery {
        UserAccessKeys.selectAll().where { UserAccessKeys.id eq id }.map { it.toUserAccessKey() }.firstOrNull()
    }

    override suspend fun getKeysForUser(userId: UUID): List<UserAccessKey> = dbQuery {
        UserAccessKeys.selectAll().where { UserAccessKeys.user eq userId }.map { it.toUserAccessKey() }
    }

    override suspend fun getKeyByValue(value: String): UserAccessKey? = dbQuery {
        UserAccessKeys.selectAll().where { UserAccessKeys.key eq value }.firstNotNullOfOrNull { it.toUserAccessKey() }
    }

    private fun ResultRow.toUser(): User {
        val userId = this[Users.id].value
        val keys = UserAccessKeys.selectAll().where { UserAccessKeys.user eq userId }.map { it.toUserAccessKey() }
        return User(
            id = userId,
            firstName = this[Users.firstName],
            lastName = this[Users.lastName],
            email = this[Users.email],
            type = this[Users.type],
            isAuthorized = this[Users.isAuthorized],
            keys = keys,
            createdAt = this[Users.createdAt],
            lastModifiedAt = this[Users.lastModifiedAt]
        )
    }

    private fun ResultRow.toUserAccessKey(): UserAccessKey {
        return UserAccessKey(
            userId = this[UserAccessKeys.user].value,
            key = this[UserAccessKeys.key],
            id = this[UserAccessKeys.id].value,
            type = this[UserAccessKeys.type],
            name = this[UserAccessKeys.name],
            createdAt = this[UserAccessKeys.createdAt],
            lastModifiedAt = this[UserAccessKeys.lastModifiedAt],
        )
    }
}

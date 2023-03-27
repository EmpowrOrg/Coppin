package org.empowrco.coppin.users.backend

import org.empowrco.coppin.models.User
import org.empowrco.coppin.models.UserAccessKey
import org.empowrco.coppin.sources.UsersSource
import java.util.UUID

interface UsersRepository {
    suspend fun getUser(id: UUID): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User)
    suspend fun getUsers(): List<User>
    suspend fun updateUser(user: User): Boolean
    suspend fun createKey(key: UserAccessKey)
    suspend fun deleteKey(id: UUID): Boolean
    suspend fun getKey(id: UUID): UserAccessKey?
}

internal class RealUsersRepository(
    private val usersSource: UsersSource,
) : UsersRepository {
    override suspend fun getUser(id: UUID): User? {
        return usersSource.getUser(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return usersSource.getUserByEmail(email)
    }

    override suspend fun createUser(user: User) {
        usersSource.createUser(user)
    }

    override suspend fun getUsers(): List<User> {
        return usersSource.getUsers()
    }

    override suspend fun updateUser(user: User): Boolean {
        return usersSource.updateUser(user)
    }

    override suspend fun createKey(key: UserAccessKey) {
        return usersSource.createKey(key)
    }

    override suspend fun deleteKey(id: UUID): Boolean {
        return usersSource.deleteKey(id)
    }

    override suspend fun getKey(id: UUID): UserAccessKey? {
        return usersSource.getKey(id)
    }
}

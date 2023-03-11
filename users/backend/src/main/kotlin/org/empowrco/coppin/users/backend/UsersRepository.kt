package org.empowrco.coppin.users.backend

import org.empowrco.coppin.models.User
import org.empowrco.coppin.sources.UsersSource
import java.util.UUID

interface UsersRepository {
    suspend fun getUser(id: UUID): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User)
    suspend fun getUsers(): List<User>
    suspend fun updateUser(user: User): Boolean
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
}

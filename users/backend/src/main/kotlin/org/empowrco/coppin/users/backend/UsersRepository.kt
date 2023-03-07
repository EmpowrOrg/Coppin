package org.empowrco.coppin.users.backend

import java.util.UUID

interface UsersRepository {
    suspend fun getUser(id: UUID): User
}

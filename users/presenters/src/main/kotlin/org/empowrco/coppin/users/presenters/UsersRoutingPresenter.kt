package org.empowrco.coppin.users.presenters

import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.User
import org.empowrco.coppin.models.UserAccessKey
import org.empowrco.coppin.users.backend.UsersRepository
import org.empowrco.coppin.utils.capitalize
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.monthDayYear
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID

interface UsersRoutingPresenter {
    suspend fun getLogin(): Result<GetLoginResponse>
    suspend fun getUsers(request: GetUsersRequest): Result<GetUsersResponse>
    suspend fun getUser(request: GetUserRequest): Result<GetUserResponse>
    suspend fun getCurrentUser(request: GetCurrentUserRequest): Result<GetCurrentUserResponse>
    suspend fun updateUser(request: UpdateUserRequest): Result<PatchUserResponse>
    suspend fun createKey(request: CreateAccessKey): Result<CreateKeyResponse>
    suspend fun deleteKey(request: DeleteAccessKey): Result<DeleteKeyResponse>
    suspend fun isUserInfoComplete(request: IsUserCompleteRequest): IsUserCompleteResponse
}

class RealUsersRoutingPresenter(
    private val repo: UsersRepository,
) : UsersRoutingPresenter {

    override suspend fun getLogin(): Result<GetLoginResponse> {
        val security = repo.getSecuritySettings()
        return GetLoginResponse(security.oktaEnabled, security.oktaDomain, "", security.oktaClientId).toResult()
    }

    override suspend fun getUsers(request: GetUsersRequest): Result<GetUsersResponse> {
        request.email ?: return failure("Unauthorized user")
        val user = repo.getUserByEmail(request.email) ?: return failure("Unauthorized user")
        if (user.type != User.Type.Admin) {
            return failure("Unauthorized user")
        }
        val users = repo.getUsers()
        return GetUsersResponse(
            users = users.map {
                GetUsersResponse.User(
                    id = it.id.toString(),
                    name = it.fullName,
                    authorized = it.isAuthorized,
                )
            },
            usersCount = users.size,
        ).toResult()
    }

    override suspend fun getCurrentUser(request: GetCurrentUserRequest): Result<GetCurrentUserResponse> {
        val user = repo.getUserByEmail(request.email) ?: return failure("No user found")
        return GetCurrentUserResponse(user.id.toString()).toResult()
    }

    override suspend fun getUser(request: GetUserRequest): Result<GetUserResponse> {
        val userId = request.id.toUuid() ?: return failure("Invalid User Id")
        val user = repo.getUser(userId) ?: return failure("No user found")
        val currentUser = repo.getUserByEmail(request.currentUser) ?: return failure("No Current User Found")
        if (currentUser.id != user.id && currentUser.type != User.Type.Admin) {
            return failure("Unauthorized access to user")
        }
        return GetUserResponse(
            id = user.id.toString(),
            firstName = user.firstName,
            lastName = user.lastName,
            authorized = user.isAuthorized,
            email = user.email,
            type = user.type.name,
            keys = user.keys.sortedBy { it.name }.map {
                GetUserResponse.Key(
                    id = it.id.toString(),
                    key = it.key,
                    name = it.name,
                    createdAt = it.createdAt.monthDayYear(),
                )
            },
            hasKeys = user.keys.isNotEmpty(),
            isAdmin = currentUser.type == User.Type.Admin,
        ).toResult()
    }

    override suspend fun updateUser(request: UpdateUserRequest): Result<PatchUserResponse> {
        val userId = request.id.toUuid() ?: return failure("No user found")
        val user = repo.getUser(userId) ?: return failure("No user found")
        val type = try {
            User.Type.valueOf(request.type.capitalize())
        } catch (ex: Exception) {
            return failure("Invalid User Type")
        }
        val updatedUser = user.copy(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            type = type,
            isAuthorized = request.authorized.toBoolean(),
            lastModifiedAt = LocalDateTime.now(),
        )
        val result = repo.updateUser(updatedUser)
        if (!result) {
            return failure("Something went wrong")
        }
        return PatchUserResponse.toResult()
    }

    override suspend fun createKey(request: CreateAccessKey): Result<CreateKeyResponse> {
        val userId = request.id.toUuid() ?: return failure("Invalid User Id")
        repo.getUser(userId) ?: return failure("User Not Found")
        val randomKey = generateRandomKey()
        val keyId = UUID.randomUUID()
        val prefix = Base64.getEncoder().encodeToString(keyId.toString().toByteArray(Charsets.UTF_8))
        val suffix = Base64.getEncoder().encodeToString(randomKey.toByteArray(Charsets.UTF_8))
        val key = "$prefix.$suffix"
        val currentTime = LocalDateTime.now()
        val type = try {
            UserAccessKey.Type.valueOf(request.type)
        } catch (ex: Exception) {
            return failure("Invalid Key Type")
        }
        val accessKey = UserAccessKey(
            userId = userId,
            id = keyId,
            key = key,
            type = type,
            name = request.name,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        repo.createKey(accessKey)
        return CreateKeyResponse(key).toResult()
    }

    private fun generateRandomKey(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        val random = SecureRandom();
        val sb = StringBuilder();
        for (i in 0 until 32) {
            val randomIndex = random.nextInt(chars.length)
            sb.append(chars[randomIndex])
        }
        return sb.toString();
    }

    override suspend fun deleteKey(request: DeleteAccessKey): Result<DeleteKeyResponse> {
        val userId = request.userId.toUuid() ?: return failure("Invalid user id")
        val keyId = request.id.toUuid() ?: return failure("Invalid key id")
        repo.getUser(userId) ?: return failure("User not found")
        val result = repo.deleteKey(keyId)
        if (!result) {
            return failure("Error deleting key")
        }
        return DeleteKeyResponse.toResult()
    }

    override suspend fun isUserInfoComplete(request: IsUserCompleteRequest): IsUserCompleteResponse {
        val user = repo.getUserByEmail(request.email) ?: throw NotFoundException("User not found")
        if (user.firstName.isBlank() || user.lastName.isBlank() || user.email.isBlank()) {
            return IsUserCompleteResponse(false)
        }
        return IsUserCompleteResponse(true)
    }
}

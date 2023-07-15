package org.empowrco.coppin.users.presenters

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.User
import org.empowrco.coppin.models.UserAccessKey
import org.empowrco.coppin.users.backend.UsersRepository
import org.empowrco.coppin.utils.DuplicateKeyException
import org.empowrco.coppin.utils.authenticator.Authenticator
import org.empowrco.coppin.utils.capitalize
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.monthDayYear
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID

interface UsersPresenters {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
    suspend fun getUsers(): Result<GetUsersResponse>
    suspend fun getUser(request: GetUserRequest): Result<GetUserResponse>
    suspend fun updateUser(request: UpdateUserRequest): Result<PatchUserResponse>
    suspend fun createKey(request: CreateAccessKey): Result<CreateKeyResponse>
    suspend fun deleteKey(request: DeleteAccessKey): Result<DeleteKeyResponse>
}

class RealUsersPresenters(
    private val repo: UsersRepository,
    private val authenticator: Authenticator,
) : UsersPresenters {
    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        if (request.email.isBlank()) {
            return failure("Please insert an email")
        } else if (request.password.isBlank()) {
            return failure("Please insert a password")
        }
        val user = repo.getUserByEmail(request.email) ?: return failure("No user found with this email")
        val passwordHash = authenticator.hash(request.password)
        if (passwordHash != user.passwordHash) {
            return failure("Incorrect password")
        } else if (!user.isAuthorized) {
            return failure("Your account has not been authorized. Pleased contact your administrator.")
        }
        return LoginResponse(user.id.toString(), user.type == User.Type.Admin).toResult()
    }

    override suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        if (request.firstName.isBlank()) {
            return failure("Please enter your first name")
        } else if (request.lastName.isBlank()) {
            return failure("Please enter your last name")
        } else if (request.email.isBlank()) {
            return failure("Please enter your email")
        } else if (request.password.isBlank()) {
            return failure("Please enter your password")
        } else if (request.confirmPassword.isBlank()) {
            return failure("Please confirm your password")
        } else if (request.password != request.confirmPassword) {
            return failure("Please ensure your passwords match")
        } else if (!request.email.isEmail()) {
            return failure("Please enter a valid email")
        }
        authenticator.isValidPassword(request.password).onFailure {
            return failure("Please enter a valid password")
        }
        val passwordHash = authenticator.hash(request.password)
        val currentTime = LocalDateTime.now()
        val user = User(
            id = UUID.randomUUID(),
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            type = User.Type.Teacher,
            isAuthorized = false,
            keys = emptyList(),
            passwordHash = passwordHash,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        try {
            repo.createUser(user)
        } catch (ex: DuplicateKeyException) {
            return failure("An account with this info already exists")
        }
        return RegisterResponse(user.id.toString(), user.type == User.Type.Admin).toResult()
    }

    private fun String.isEmail(): Boolean {
        return "^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$".toRegex().matches(this)
    }

    override suspend fun getUsers(): Result<GetUsersResponse> {
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

    override suspend fun getUser(request: GetUserRequest): Result<GetUserResponse> {
        val userId = request.id.toUuid() ?: return failure("Invalid User Id")
        val user = repo.getUser(userId) ?: return failure("No user found")
        val currentUserId = request.currentUserId.toUuid() ?: return failure("Invalid Current User Id")
        val currentUser = repo.getUser(currentUserId) ?: return failure("No Current User Found")
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
        val user = repo.getUser(userId) ?: return failure("User Not Found")
        val passwordHash = authenticator.hash(request.password)
        if (user.passwordHash != passwordHash) {
            return failure("Invalid password")
        }
        val randomKey = generateRandomPassword()
        val keyId = UUID.randomUUID()
        val prefix = Base64.getEncoder().encodeToString(keyId.toString().toByteArray(Charsets.UTF_8))
        val suffix = Base64.getEncoder().encodeToString(randomKey.toByteArray(Charsets.UTF_8))
        val key = "$prefix.$suffix"
        val currentTime = LocalDateTime.now()
        val accessKey = UserAccessKey(
            userId = userId,
            id = keyId,
            key = key,
            name = request.name,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        repo.createKey(accessKey)
        return CreateKeyResponse(key).toResult()
    }

    private fun generateRandomPassword(): String {
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
        val user = repo.getUser(userId) ?: return failure("User not found")
        val passwordHash = authenticator.hash(request.password)
        if (passwordHash != user.passwordHash) {
            return failure("Invalid password")
        }
        val result = repo.deleteKey(keyId)
        if (!result) {
            return failure("Error deleting key")
        }
        return DeleteKeyResponse.toResult()
    }
}

package org.empowrco.coppin.users.presenters

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.User
import org.empowrco.coppin.users.backend.UsersRepository
import org.empowrco.coppin.utils.DuplicateKeyException
import org.empowrco.coppin.utils.authenticator.Authenticator
import org.empowrco.coppin.utils.capitalize
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

interface UsersPresenters {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
    suspend fun getUsers(): Result<GetUsersResponse>
    suspend fun getUser(request: GetUserRequest): Result<GetUserResponse>
    suspend fun updateUser(request: PatchUserRequest): Result<PatchUserResponse>
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
        }
        return LoginResponse.toResult()
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
            passwordHash = passwordHash,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        try {
            repo.createUser(user)
        } catch (ex: DuplicateKeyException) {
            return failure("An account with this info already exists")
        }
        return RegisterResponse.toResult()
    }

    private fun String.isEmail(): Boolean {
        return "^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$".toRegex().matches(this)
    }

    override suspend fun getUsers(): Result<GetUsersResponse> {
        return GetUsersResponse(
            users = repo.getUsers().map {
                GetUsersResponse.User(
                    id = it.id.toString(),
                    email = it.email,
                    name = it.fullName,
                    authorized = it.isAuthorized,
                    type = it.type.name,
                )
            }
        ).toResult()
    }

    override suspend fun getUser(request: GetUserRequest): Result<GetUserResponse> {
        val userId = request.id.toUuid() ?: return failure("No user found")
        val user = repo.getUser(userId) ?: return failure("No user found")
        return GetUserResponse(
            id = user.id.toString(),
            firstName = user.firstName,
            lastName = user.lastName,
            authorized = user.isAuthorized,
            email = user.email,
            type = user.type.name,
        ).toResult()
    }

    override suspend fun updateUser(request: PatchUserRequest): Result<PatchUserResponse> {
        val userId = request.id.toUuid() ?: return failure("No user found")
        val user = repo.getUser(userId) ?: return failure("No user found")
        val type = try {
            User.Type.valueOf(request.type.capitalize())
        } catch (ex: Exception) {
            return failure("Invalid type")
        }
        val updatedUser = user.copy(
            firstName = user.firstName,
            lastName = user.lastName,
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
}

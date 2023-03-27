package org.empowrco.coppin.users.presenters

import kotlinx.serialization.Serializable

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)

data class GetUserRequest(
    val id: String,
)


data class UpdateUserRequest(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val type: String,
    val authorized: String,
)

@Serializable
data class CreateAccessKey(
    val id: String,
    val password: String,
)

@Serializable
data class DeleteAccessKey(
    val userId: String,
    val id: String,
    val password: String,
)

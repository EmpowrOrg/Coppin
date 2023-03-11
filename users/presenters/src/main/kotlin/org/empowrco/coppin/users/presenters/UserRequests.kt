package org.empowrco.coppin.users.presenters

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

data class PatchUserRequest(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val type: String,
    val authorized: String,
)

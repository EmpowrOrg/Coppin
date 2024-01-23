package org.empowrco.coppin.users.presenters

import kotlinx.serialization.Serializable


data class GetLoginResponse(val okta: Boolean)
data class LoginResponse(val id: String, val isAdmin: Boolean)

data class RegisterResponse(val id: String, val isAdmin: Boolean)

data class GetUsersResponse(
    val users: List<User>,
    val usersCount: Int,
) {
    data class User(
        val id: String,
        val name: String,
        val authorized: Boolean,
    )
}

data class GetUserResponse(
    val id: String,
    val authorized: Boolean,
    val email: String,
    val type: String,
    val firstName: String,
    val lastName: String,
    val hasKeys: Boolean,
    val keys: List<Key>?,
    val isAdmin: Boolean,
) {
    data class Key(val name: String, val key: String, val id: String, val createdAt: String)
}


object PatchUserResponse

@Serializable
data class CreateKeyResponse(val key: String)

@Serializable
object DeleteKeyResponse

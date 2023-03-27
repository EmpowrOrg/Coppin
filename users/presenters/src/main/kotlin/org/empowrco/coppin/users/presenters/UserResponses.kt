package org.empowrco.coppin.users.presenters

import kotlinx.serialization.Serializable


data class LoginResponse(val id: String, val isAdmin: Boolean)

data class RegisterResponse(val id: String, val isAdmin: Boolean)

data class GetUsersResponse(
    val users: List<User>,
) {
    data class User(
        val id: String,
        val name: String,
        val email: String,
        val authorized: Boolean,
        val type: String,
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
) {
    data class Key(val key: String, val id: String)
}


object PatchUserResponse

@Serializable
data class CreateKeyResponse(val key: String)

@Serializable
object DeleteKeyResponse

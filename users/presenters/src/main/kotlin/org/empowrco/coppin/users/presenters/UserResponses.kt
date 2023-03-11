package org.empowrco.coppin.users.presenters

object LoginResponse

object RegisterResponse

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
)

object PatchUserResponse

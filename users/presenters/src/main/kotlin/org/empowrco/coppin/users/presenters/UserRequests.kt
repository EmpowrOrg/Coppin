package org.empowrco.coppin.users.presenters

import kotlinx.serialization.Serializable

data class GetUserRequest(
    val id: String,
    val currentUser: String,
)

data class GetCurrentUserRequest(
    val email: String,
)

data class GetUsersRequest(
    val email: String?,
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
    val email: String,
    val name: String,
    val type: String,
)

@Serializable
data class DeleteAccessKey(
    val userId: String,
    val id: String,
    val email: String,
)

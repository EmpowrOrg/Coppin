package org.empowrco.coppin.models

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val type: Type,
    val isAuthorized: Boolean,
    val keys: List<UserAccessKey>,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
) {

    val fullName: String = "$firstName $lastName"

    enum class Type {
        Admin, Teacher, Student, IT
    }
}

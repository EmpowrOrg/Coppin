package org.empowrco.coppin.courses.presenters.api

import kotlinx.serialization.Serializable

@Serializable
data class GetStudentAssignmentsRequest(
    val courseId: String,
    val studentId: String,
)

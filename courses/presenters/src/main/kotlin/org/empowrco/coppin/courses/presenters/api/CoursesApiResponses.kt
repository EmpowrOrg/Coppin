package org.empowrco.coppin.courses.presenters.api

import kotlinx.serialization.Serializable

@Serializable
data class GetStudentAssignmentsResponse(
    val assignments: List<Assignment>,
) {

    @Serializable
    data class Assignment(
        val score: Int,
        val feedback: String,
        val code: String,
        val language: Language,
        val attempts: Int,
        val assignmentId: String,
    ) {
        @Serializable
        data class Language(
            val mime: String,
            val name: String,
        )
    }

}
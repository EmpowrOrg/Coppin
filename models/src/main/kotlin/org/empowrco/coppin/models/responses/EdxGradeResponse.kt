package org.empowrco.coppin.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class EdxGradeResponse(val results: List<Grade>) {
    @Serializable
    data class Grade(
        val username: String,
        val email: String,
        val passed: Boolean,
        val percent: Double,
        val letterGrade: String?,
    )
}

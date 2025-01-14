package org.empowrco.coppin.courses.presenters.api

import kotlinx.serialization.Serializable

@Serializable
data class GetStudentAssignmentsResponse(
    val assignments: List<Assignment>,
) {

    @Serializable
    data class Assignment(
        val title: String,
        val score: Int,
        val feedback: String,
        val code: String,
        val language: Language,
        val attempts: Int,
        val id: String,
    ) {
        @Serializable
        data class Language(
            val mime: String,
            val name: String,
        )
    }

}

@Serializable
data class GetCourseResponse(
    val id: String,
    val name: String,
    val completionRate: Int,
    val chart: Chart,
    val subjects: List<Subject>,
    val assignments: List<Assignment>,
) {

    @Serializable
    data class Subject(
        val id: String,
        val title: String,
        val completionRate: Int,
        val successRate: Int,
        val numberOfAssignments: Int,
    )

    @Serializable
    data class Assignment(
        val id: String,
        val title: String,
        val successRate: Int,
        val completionRate: Int,
        val subject: String,
        val dueDate: String?,
        val courseId: String,
    )

    @Serializable
    data class Chart(
        val title: String,
        val y: Y,
        val x: X,
    ) {
        @Serializable
        data class Y(val label: String, val min: Int, val max: Int)

        @Serializable
        data class X(val labels: List<String>, val lines: List<Line>) {
            @Serializable
            data class Line(val name: String, val points: List<Double>, val color: String)
        }
    }
}

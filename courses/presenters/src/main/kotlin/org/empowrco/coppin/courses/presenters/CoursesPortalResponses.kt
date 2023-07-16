package org.empowrco.coppin.courses.presenters

import kotlinx.serialization.Serializable

data class GetCoursesResponse(val courses: List<Course>, val coursesCount: Int) {
    data class Course(
        val id: String,
        val name: String,
        val number: String,
        val startDate: String,
        val endDate: String,
    )
}


data class GetUnlinkedCoursesResponse(val count: Int, val rows: List<CourseRow>) {

    data class CourseRow(val one: Course, val two: Course?, val three: Course?)
    data class Course(
        val id: String,
        val name: String,
        val number: String,
        val dates: String,
        val org: String,
    )
}

data class GetCourseResponse(
    val id: String,
    val name: String,
    val referenceId: String,
    val assignments: List<Assignment>,
    val subjects: List<Subject>,
    val chart: Chart,
) {

    data class Chart(
        val title: String,
        val y: Y,
        val x: X,
    ) {
        data class Y(val label: String, val min: Int, val max: Int)
        data class X(val labels: List<String>, val lines: List<Line>) {
            data class Line(val name: String, val points: List<Int>)
        }
    }

    data class Assignment(
        val id: String,
        val title: String,
        val successRate: String,
        val completionRate: String,
        val subject: String,
        val lastModified: String,
    )

    data class Subject(
        val id: String,
        val name: String,
        val assignments: String,
        val lastModified: String,
    )
}

@Serializable
object CreateSubjectResponse

data class GetSubjectResponse(
    val id: String,
    val courseId: String,
    val name: String,
    val canBeDeleted: Boolean,
)

object UpdateSubjectResponse

@Serializable
object DeleteSubjectResponse

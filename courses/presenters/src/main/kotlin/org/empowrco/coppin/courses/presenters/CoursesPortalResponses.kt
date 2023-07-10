package org.empowrco.coppin.courses.presenters

data class GetCoursesResponse(val courses: List<Course>, val coursesCount: Int) {
    data class Course(
        val id: String,
        val name: String,
        val number: String,
        val startDate: String,
        val endDate: String,
    )
}

data class GetCourseResponse(
    val id: String,
    val name: String,
    val referenceId: String,
    val assignments: List<Assignment>,
) {

    data class Assignment(
        val id: String,
        val title: String,
        val successRate: String,
        val completionRate: String,
        val lastModified: String,
    )
}

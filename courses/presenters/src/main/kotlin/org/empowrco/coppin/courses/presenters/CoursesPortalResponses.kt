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
) {

    data class Assignment(
        val id: String,
        val title: String,
        val successRate: String,
        val completionRate: String,
        val lastModified: String,
    )
}

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
    val name: String,
    val referenceId: String,
    val completionRate: String,
    val students: List<Student>,
    val gradingSummaries: List<GradingSummary>,
    val assignments: List<Assignment>,
) {
    data class GradingSummary(val type: String, val weight: String, val grade: String, val weightGrade: String)
    data class Student(
        val name: String,
        val lab: String,
        val homework: String,
        val finalExam: String,
        val engagement: String,
    )

    data class Assignment(
        val id: String,
        val title: String,
        val successRate: String,
        val lastModified: String,
    )
}

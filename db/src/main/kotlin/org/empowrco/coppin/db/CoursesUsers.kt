package org.empowrco.coppin.db

object CoursesUsers : BaseTable() {
    val course = reference("course_id", Courses.id)
    val user = reference("user_id", Users.id)

    init {
        uniqueIndex(course, user)
    }
}

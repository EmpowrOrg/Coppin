package org.empowrco.coppin.db

object Subjects : BaseTable() {
    val course = reference("course", Courses.id)
    val name = text("name")

    init {
        uniqueIndex(course, name)
    }
}

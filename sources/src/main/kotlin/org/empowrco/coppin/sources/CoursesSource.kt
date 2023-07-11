package org.empowrco.coppin.sources

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.db.Courses
import org.empowrco.coppin.db.CoursesUsers
import org.empowrco.coppin.models.Course
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface CoursesSource {
    suspend fun createCourse(course: Course)
    suspend fun getCourse(id: UUID): Course?
    suspend fun getCourses(): List<Course>
    suspend fun getCourseByEdxId(id: String): Course?
    suspend fun getLinkedCourses(userId: UUID): List<Course>
    suspend fun getUnlinkedCourses(userId: UUID): List<Course>
    suspend fun deleteCourse(id: UUID): Boolean
    suspend fun updateCourse(course: Course): Boolean
    suspend fun linkCourse(courseId: UUID, userId: UUID, currentTime: LocalDateTime)
}

internal class RealCoursesSource : CoursesSource {
    override suspend fun createCourse(course: Course) = dbQuery {
        Courses.insert {
            it.build(course)
        }
        Unit
    }

    override suspend fun getCourse(id: UUID): Course? = dbQuery {
        Courses.select { Courses.id eq id }.limit(1).map { it.toCourse() }.firstOrNull()
    }

    override suspend fun getCourseByEdxId(id: String): Course? = dbQuery {
        Courses.select { Courses.edxId eq id }.limit(1).map { it.toCourse() }.firstOrNull()
    }

    override suspend fun getCourses(): List<Course> = dbQuery {
        Courses.selectAll().map { it.toCourse() }
    }

    override suspend fun getLinkedCourses(userId: UUID): List<Course> = dbQuery {
        val courseIds = CoursesUsers.select { CoursesUsers.user eq userId }.map { it[CoursesUsers.course].value }
        Courses.select { Courses.id inList courseIds }.map { it.toCourse() }
    }

    override suspend fun linkCourse(courseId: UUID, userId: UUID, currentTime: LocalDateTime) = dbQuery {
        CoursesUsers.insert {
            it[CoursesUsers.course] = courseId
            it[CoursesUsers.user] = userId
            it[CoursesUsers.createdAt] = currentTime
            it[CoursesUsers.lastModifiedAt] = currentTime
        }
        Unit
    }

    override suspend fun getUnlinkedCourses(userId: UUID): List<Course> = dbQuery {
        val courseIds = CoursesUsers.select { CoursesUsers.user eq userId }.map { it[CoursesUsers.course].value }
        Courses.select { Courses.id notInList courseIds }.map { it.toCourse() }
    }

    override suspend fun deleteCourse(id: UUID): Boolean = dbQuery {
        Courses.deleteWhere { Courses.id eq id } > 0
    }

    override suspend fun updateCourse(course: Course) = dbQuery {
        Courses.update({ Courses.id eq course.id }) {
            it.build(course)
        } > 0
    }

    private fun UpdateBuilder<*>.build(course: Course) {
        this[Courses.id] = course.id
        this[Courses.createdAt] = course.createdAt
        this[Courses.edxId] = course.edxId
        this[Courses.title] = course.title
        this[Courses.number] = course.number
        this[Courses.org] = course.org
        this[Courses.lastModifiedAt] = course.lastModifiedAt
    }

    private fun ResultRow.toCourse(): Course {
        return Course(
            id = this[Courses.id].value,
            title = this[Courses.title],
            edxId = this[Courses.edxId],
            number = this[Courses.number],
            org = this[Courses.org],
            createdAt = this[Courses.createdAt],
            lastModifiedAt = this[Courses.lastModifiedAt],
        )
    }

}

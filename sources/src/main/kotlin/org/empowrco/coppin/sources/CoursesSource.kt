package org.empowrco.coppin.sources

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Courses
import org.empowrco.coppin.db.CoursesUsers
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
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
    suspend fun deleteCourse(course: Course): Boolean
    suspend fun updateCourse(course: Course): Boolean
    suspend fun linkCourse(courseId: UUID, userId: UUID, currentTime: LocalDateTime)
    suspend fun unlinkCoursesNotIn(courseIds: List<UUID>, userId: UUID)
}

internal class RealCoursesSource(cache: Cache) : CoursesSource {

    private val cache = CacheCoursesSource(cache)
    private val database = DatabaseCoursesSource()
    override suspend fun createCourse(course: Course) {
        database.createCourse(course)
        cache.createCourse(course)
    }

    override suspend fun getCourse(id: UUID): Course? {
        return cache.getCourse(id) ?: database.getCourse(id)?.also {
            cache.createCourse(it)
        }
    }

    override suspend fun getCourses(): List<Course> {
        return cache.getCourses().ifEmpty {
            database.getCourses().also {
                cache.saveCourses(it)
            }
        }
    }

    override suspend fun getCourseByEdxId(id: String): Course? {
        return cache.getCourseByEdxId(id) ?: database.getCourseByEdxId(id)?.also {
            cache.createCourse(it)
        }
    }

    override suspend fun getLinkedCourses(userId: UUID): List<Course> {
        return database.getLinkedCourses(userId)
    }

    override suspend fun deleteCourse(course: Course): Boolean {
        cache.deleteCourse(course)
        return database.deleteCourse(course)
    }

    override suspend fun updateCourse(course: Course): Boolean {
        cache.updateCourse(course)
        return database.updateCourse(course)
    }

    override suspend fun linkCourse(courseId: UUID, userId: UUID, currentTime: LocalDateTime) {
        database.linkCourse(courseId, userId, currentTime)
    }

    override suspend fun unlinkCoursesNotIn(courseIds: List<UUID>, userId: UUID) {
        database.unlinkCoursesNotIn(courseIds, userId)
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheCoursesSource(private val cache: Cache) : CoursesSource {

    private fun courseKey(id: UUID?, edxId: String?) = "course:$id:$edxId"
    private fun coursesKey() = "courses"

    override suspend fun createCourse(course: Course) {
        cache.set(courseKey(course.id, null), json.encodeToString(course))
        cache.set(courseKey(null, course.edxId), json.encodeToString(course))
        cache.delete(coursesKey())
    }


    override suspend fun getCourse(id: UUID): Course? {
        return cache.get(courseKey(id, null), Course::class.serializer())
    }

    override suspend fun getCourses(): List<Course> {
        return cache.getList(coursesKey(), Course::class.serializer())
    }

    suspend fun saveCourses(courses: List<Course>) {
        return cache.set(coursesKey(), json.encodeToString(courses))
    }

    override suspend fun getCourseByEdxId(id: String): Course? {
        return cache.get(courseKey(null, id), Course::class.serializer())
    }

    override suspend fun getLinkedCourses(userId: UUID): List<Course> {
        throw NotImplementedError("Use Database")
    }

    override suspend fun unlinkCoursesNotIn(courseIds: List<UUID>, userId: UUID) {
        throw NotImplementedError("Use Database")
    }

    override suspend fun deleteCourse(course: Course): Boolean {
        cache.delete(courseKey(course.id, null))
        cache.delete(courseKey(null, course.edxId))
        cache.delete(coursesKey())
        return true
    }

    override suspend fun updateCourse(course: Course): Boolean {
        cache.delete(coursesKey())
        cache.delete(courseKey(course.id, null))
        cache.delete(courseKey(null, course.edxId))
        return true
    }

    override suspend fun linkCourse(courseId: UUID, userId: UUID, currentTime: LocalDateTime) {
        throw NotImplementedError("Use Database")
    }
}

private class DatabaseCoursesSource : CoursesSource {
    override suspend fun createCourse(course: Course) = dbQuery {
        Courses.insert {
            it.build(course)
        }
        Unit
    }

    override suspend fun getCourse(id: UUID): Course? = dbQuery {
        Courses.selectAll().where { Courses.id eq id }.limit(1).map { it.toCourse() }.firstOrNull()
    }

    override suspend fun getCourseByEdxId(id: String): Course? = dbQuery {
        Courses.selectAll().where { Courses.edxId eq id }.limit(1).map { it.toCourse() }.firstOrNull()
    }

    override suspend fun getCourses(): List<Course> = dbQuery {
        Courses.selectAll().map { it.toCourse() }
    }

    override suspend fun getLinkedCourses(userId: UUID): List<Course> = dbQuery {
        val courseIds =
            CoursesUsers.selectAll().where { CoursesUsers.user eq userId }.map { it[CoursesUsers.course].value }
        Courses.selectAll().where { Courses.id inList courseIds }.map { it.toCourse() }
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

    override suspend fun unlinkCoursesNotIn(courseIds: List<UUID>, userId: UUID) = dbQuery {
        CoursesUsers.deleteWhere {
            (CoursesUsers.course notInList courseIds) and (CoursesUsers.user eq userId)
        }
        Unit
    }

    override suspend fun deleteCourse(course: Course): Boolean = dbQuery {
        Courses.deleteWhere { Courses.id eq course.id } > 0
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

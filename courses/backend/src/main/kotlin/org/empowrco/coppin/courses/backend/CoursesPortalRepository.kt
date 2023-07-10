package org.empowrco.coppin.courses.backend

import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.models.responses.EdxCoursesResponse
import org.empowrco.coppin.models.responses.EdxGradeResponse
import org.empowrco.coppin.sources.CoursesSource
import org.empowrco.coppin.sources.EdxSource
import java.util.UUID

interface CoursesPortalRepository {
    suspend fun getCourses(): List<Course>
    suspend fun createCourse(course: Course)
    suspend fun updateCourse(course: Course): Boolean
    suspend fun deleteCourse(id: UUID): Boolean
    suspend fun getCourse(id: UUID): Course?
    suspend fun getEdxCourse(id: String): Result<EdxCourse>
    suspend fun getEdxCourses(): Result<EdxCoursesResponse>
    suspend fun getGrades(id: String): Result<EdxGradeResponse>
}

internal class RealCoursesPortalRepository(
    private val coursesSource: CoursesSource,
    private val edxSource: EdxSource,
) : CoursesPortalRepository {

    override suspend fun getCourses(): List<Course> {
        return coursesSource.getCourses()
    }

    override suspend fun updateCourse(course: Course): Boolean {
        return coursesSource.updateCourse(course)
    }

    override suspend fun createCourse(course: Course) {
        coursesSource.createCourse(course)
    }

    override suspend fun deleteCourse(id: UUID): Boolean {
        return coursesSource.deleteCourse(id)
    }

    override suspend fun getEdxCourses(): Result<EdxCoursesResponse> {
        return edxSource.getCourses()
    }

    override suspend fun getEdxCourse(id: String): Result<EdxCourse> {
        return edxSource.getCourse(id)
    }

    override suspend fun getCourse(id: UUID): Course? {
        return coursesSource.getCourse(id)
    }

    override suspend fun getGrades(id: String): Result<EdxGradeResponse> {
        return edxSource.getGrades(id)
    }


}

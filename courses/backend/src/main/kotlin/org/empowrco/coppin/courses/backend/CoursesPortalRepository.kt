package org.empowrco.coppin.courses.backend

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.models.responses.EdxCoursesResponse
import org.empowrco.coppin.models.responses.EdxEnrollmentsResponse
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.CoursesSource
import org.empowrco.coppin.sources.EdxSource
import org.empowrco.coppin.sources.SubmissionSource
import java.util.UUID

interface CoursesPortalRepository {
    suspend fun getLinkedCourses(userId: UUID): List<Course>
    suspend fun getUnlinkedCourses(userId: UUID): List<Course>
    suspend fun linkCourse(courseId: UUID, userId: UUID, currentTime: LocalDateTime)
    suspend fun createCourse(course: Course)
    suspend fun updateCourse(course: Course): Boolean
    suspend fun deleteCourse(id: UUID): Boolean
    suspend fun getCourse(id: UUID): Course?
    suspend fun getCourseByEdxId(id: String): Course?
    suspend fun getEdxCourse(id: String): Result<EdxCourse>
    suspend fun getEdxCourses(): Result<EdxCoursesResponse>
    suspend fun getAssignmentsForCourse(courseId: UUID): List<Assignment>
    suspend fun getLastStudentSubmissionForAssignment(id: UUID): List<Submission>
    suspend fun getStudentsForCourse(edxId: String): Result<EdxEnrollmentsResponse>
}

internal class RealCoursesPortalRepository(
    private val coursesSource: CoursesSource,
    private val edxSource: EdxSource,
    private val assignmentSource: AssignmentSource,
    private val submissionSource: SubmissionSource,
) : CoursesPortalRepository {

    override suspend fun getLinkedCourses(userId: UUID): List<Course> {
        return coursesSource.getLinkedCourses(userId)
    }

    override suspend fun linkCourse(courseId: UUID, userId: UUID, currentTime: LocalDateTime) {
        return coursesSource.linkCourse(courseId, userId, currentTime)
    }

    override suspend fun getCourseByEdxId(id: String): Course? {
        return coursesSource.getCourseByEdxId(id)
    }

    override suspend fun getUnlinkedCourses(userId: UUID): List<Course> {
        return coursesSource.getUnlinkedCourses(userId)
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

    override suspend fun getAssignmentsForCourse(courseId: UUID): List<Assignment> {
        return assignmentSource.getAssignmentsForCourse(courseId)
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

    override suspend fun getLastStudentSubmissionForAssignment(id: UUID): List<Submission> {
        return submissionSource.getLastStudentSubmissionForAssignment(id)
    }

    override suspend fun getStudentsForCourse(edxId: String): Result<EdxEnrollmentsResponse> {
        return edxSource.getStudentsForCourse(edxId)
    }


}

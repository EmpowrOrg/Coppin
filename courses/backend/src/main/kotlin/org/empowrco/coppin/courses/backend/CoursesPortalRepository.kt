package org.empowrco.coppin.courses.backend

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
    suspend fun getCourses(): List<Course>
    suspend fun createCourse(course: Course)
    suspend fun updateCourse(course: Course): Boolean
    suspend fun deleteCourse(id: UUID): Boolean
    suspend fun getCourse(id: UUID): Course?
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

    override suspend fun getAssignmentsForCourse(courseId: UUID): List<Assignment> {
        return assignmentSource.getAssignments()
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

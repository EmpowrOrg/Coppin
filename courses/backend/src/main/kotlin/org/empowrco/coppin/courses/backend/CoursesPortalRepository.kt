package org.empowrco.coppin.courses.backend

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.models.responses.EdxCoursesResponse
import org.empowrco.coppin.models.responses.EdxEnrollmentsResponse
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.CoursesSource
import org.empowrco.coppin.sources.EdxSource
import org.empowrco.coppin.sources.SubjectSource
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
    suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission>
    suspend fun getStudentsForCourse(edxId: String): Result<EdxEnrollmentsResponse>
    suspend fun createSubject(subject: Subject)
    suspend fun getSubjects(courseId: UUID): List<Subject>
    suspend fun getAssignmentCountBySubject(id: UUID): Long
    suspend fun getAssignmentsBySubject(id: UUID): List<Assignment>
    suspend fun getSubject(id: UUID): Subject?
    suspend fun updateSubject(subject: Subject): Boolean
    suspend fun deleteSubject(id: UUID): Boolean
}

internal class RealCoursesPortalRepository(
    private val coursesSource: CoursesSource,
    private val edxSource: EdxSource,
    private val assignmentSource: AssignmentSource,
    private val submissionSource: SubmissionSource,
    private val subjectSource: SubjectSource,
) : CoursesPortalRepository {

    override suspend fun createSubject(subject: Subject) {
        return subjectSource.createSubject(subject)
    }

    override suspend fun getSubject(id: UUID): Subject? {
        return subjectSource.getSubject(id)
    }

    override suspend fun deleteSubject(id: UUID): Boolean {
        return subjectSource.deleteSubject(id)
    }

    override suspend fun updateSubject(subject: Subject): Boolean {
        return subjectSource.updateSubject(subject)
    }

    override suspend fun getSubjects(courseId: UUID): List<Subject> {
        return subjectSource.getSubjectsForCourse(courseId)
    }

    override suspend fun getAssignmentsBySubject(id: UUID): List<Assignment> {
        return assignmentSource.getAssignmentsForSubject(id)
    }

    override suspend fun getAssignmentCountBySubject(id: UUID): Long {
        return assignmentSource.getAssignmentCountBySubject(id)
    }

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

    override suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission> {
        return submissionSource.getLatestStudentSubmissionsForAssignment(id)
    }

    override suspend fun getStudentsForCourse(edxId: String): Result<EdxEnrollmentsResponse> {
        return edxSource.getStudentsForCourse(edxId)
    }


}

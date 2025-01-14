package org.empowrco.coppin.courses.backend

import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.models.responses.EdxEnrollmentsResponse
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.CoursesSource
import org.empowrco.coppin.sources.EdxSource
import org.empowrco.coppin.sources.LanguagesSource
import org.empowrco.coppin.sources.SubjectSource
import org.empowrco.coppin.sources.SubmissionSource
import java.util.UUID

interface CoursesApiRepository {
    suspend fun getLinkedCourses(id: UUID): List<Course>
    suspend fun getLatestSubmissionForEachAssignment(courseId: UUID, studentId: String): List<Submission>
    suspend fun doesCourseExist(id: UUID): Boolean
    suspend fun getLanguage(id: UUID): Language?
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun getCourse(id: UUID): Course?
    suspend fun getAssignmentsForCourse(courseId: UUID): List<Assignment>
    suspend fun getAssignmentCountBySubject(id: UUID): Long
    suspend fun getSubjects(courseId: UUID): List<Subject>
    suspend fun getStudentsForCourse(edxId: String): Result<EdxEnrollmentsResponse>
    suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission>

}

internal class RealCoursesApiRepository(
    private val coursesSource: CoursesSource,
    private val assignmentSource: AssignmentSource,
    private val submissionSource: SubmissionSource,
    private val languagesSource: LanguagesSource,
    private val subjectsSource: SubjectSource,
    private val edxSource: EdxSource,
) : CoursesApiRepository {
    override suspend fun getLinkedCourses(id: UUID): List<Course> {
        return coursesSource.getLinkedCourses(id)
    }

    override suspend fun getLatestSubmissionForEachAssignment(courseId: UUID, studentId: String): List<Submission> {
        val assignments = assignmentSource.getAssignmentsForCourse(courseId)
        val submissions = assignments.mapNotNull {
            submissionSource.getLastStudentSubmissionForAssignment(it.id, studentId)
        }
        return submissions
    }

    override suspend fun doesCourseExist(id: UUID): Boolean {
        return coursesSource.getCourse(id) != null
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languagesSource.getLanguage(id)
    }

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignmentSource.getAssignment(id)
    }

    override suspend fun getCourse(id: UUID): Course? {
        return coursesSource.getCourse(id)
    }

    override suspend fun getAssignmentsForCourse(courseId: UUID): List<Assignment> {
        return assignmentSource.getAssignmentsForCourse(courseId)
    }

    override suspend fun getAssignmentCountBySubject(id: UUID): Long {
        return assignmentSource.getAssignmentCountBySubject(id)
    }

    override suspend fun getSubjects(courseId: UUID): List<Subject> {
        return subjectsSource.getSubjectsForCourse(courseId)
    }

    override suspend fun getStudentsForCourse(edxId: String): Result<EdxEnrollmentsResponse> {
        return edxSource.getStudentsForCourse(edxId)
    }

    override suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission> {
        return submissionSource.getLatestStudentSubmissionsForAssignment(id)
    }
}

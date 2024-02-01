package org.empowrco.coppin.assignment.backend

import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.models.User
import org.empowrco.coppin.models.responses.AiResponse
import org.empowrco.coppin.sources.AssignmentCodesSource
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.CoursesSource
import org.empowrco.coppin.sources.LanguagesSource
import org.empowrco.coppin.sources.OpenAiSource
import org.empowrco.coppin.sources.SubjectSource
import org.empowrco.coppin.sources.SubmissionSource
import org.empowrco.coppin.sources.UsersSource
import java.util.UUID


interface AssignmentPortalRepository {
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun getSubject(id: UUID): Subject?
    suspend fun getSubjectsForCourse(id: UUID): List<Subject>
    suspend fun createAssignment(assignment: Assignment)
    suspend fun getLanguage(mime: String): Language?
    suspend fun getCode(id: UUID): AssignmentCode?
    suspend fun updateCode(code: AssignmentCode): Boolean
    suspend fun saveCode(code: AssignmentCode)
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getLanguages(): List<Language>
    suspend fun deleteCode(assignmentCode: AssignmentCode)
    suspend fun deprimaryAssignmentCodes(assignment: Assignment)
    suspend fun getAssignmentCodes(id: UUID): List<AssignmentCode>
    suspend fun getCourse(id: UUID): Course?
    suspend fun getLatestStudentSubmissionForAssignment(assignmentId: UUID): List<Submission>
    suspend fun getStudentSubmissionsForAssignment(assignmentId: UUID, studentId: String): List<Submission>
    suspend fun getLanguage(id: UUID): Language?
    suspend fun getSubmission(id: UUID): Submission?
    suspend fun assignmentsWithReferenceStartingWithCount(name: String): Long
    suspend fun generateAssignment(prompt: String, userId: String): AiResponse
    suspend fun getUserByEmail(email: String): User?
    suspend fun isAiEnabled(): Boolean
}

internal class RealAssignmentPortalRepository(
    private val assignmentSource: AssignmentSource,
    private val languagesSource: LanguagesSource,
    private val codesSource: AssignmentCodesSource,
    private val coursesSource: CoursesSource,
    private val subjectSource: SubjectSource,
    private val submissionSource: SubmissionSource,
    private val openAiSource: OpenAiSource,
    private val usersSource: UsersSource,
) : AssignmentPortalRepository {

    override suspend fun getSubject(id: UUID): Subject? {
        return subjectSource.getSubject(id)
    }

    override suspend fun getAssignmentCodes(id: UUID): List<AssignmentCode> {
        return codesSource.getByAssigment(id)
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        return assignmentSource.updateAssignment(assignment)
    }

    override suspend fun getLanguages(): List<Language> {
        return languagesSource.getLanguages()
    }

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignmentSource.getAssignment(id)
    }

    override suspend fun getCode(id: UUID): AssignmentCode? {
        return codesSource.get(id)
    }

    override suspend fun getLanguage(mime: String): Language? {
        return languagesSource.getLanguageByMime(mime)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        return assignmentSource.createAssignment(assignment)
    }


    override suspend fun updateCode(code: AssignmentCode): Boolean {
        return codesSource.update(code)
    }

    override suspend fun saveCode(code: AssignmentCode) {
        codesSource.create(code)
    }

    override suspend fun deprimaryAssignmentCodes(assignment: Assignment) {
        codesSource.deprimaryAssignmentCodes(assignment)
    }

    override suspend fun deleteCode(assignmentCode: AssignmentCode) {
        codesSource.delete(assignmentCode)
    }

    override suspend fun getCourse(id: UUID): Course? {
        return coursesSource.getCourse(id)
    }

    override suspend fun getSubjectsForCourse(id: UUID): List<Subject> {
        return subjectSource.getSubjectsForCourse(id)
    }

    override suspend fun getLatestStudentSubmissionForAssignment(assignmentId: UUID): List<Submission> {
        return submissionSource.getLatestStudentSubmissionsForAssignment(assignmentId)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languagesSource.getLanguage(id)
    }

    override suspend fun getSubmission(id: UUID): Submission? {
        return submissionSource.getSubmission(id)
    }

    override suspend fun getStudentSubmissionsForAssignment(assignmentId: UUID, studentId: String): List<Submission> {
        return submissionSource.getSubmissionsForAssignment(assignmentId, studentId)
    }

    override suspend fun assignmentsWithReferenceStartingWithCount(name: String): Long {
        return assignmentSource.assignmentsWithReferenceStartingWithCount(name)
    }

    override suspend fun generateAssignment(prompt: String, userId: String): AiResponse {
        return openAiSource.prompt(prompt, userId)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return usersSource.getUserByEmail(email)
    }

    override suspend fun isAiEnabled(): Boolean {
        return openAiSource.isEnabled()
    }
}

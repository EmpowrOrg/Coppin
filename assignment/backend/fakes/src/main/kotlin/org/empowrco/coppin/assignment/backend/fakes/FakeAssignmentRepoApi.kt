package org.empowrco.coppin.assignment.backend.fakes

import org.empowrco.coppin.assignment.backend.AssignmentApiRepository
import org.empowrco.coppin.assignment.backend.AssignmentCodeResponse
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.models.responses.AiResponse
import java.util.UUID

class FakeAssignmentRepoApi : AssignmentApiRepository {

    val assignments = mutableListOf<Assignment>()
    val languages = mutableListOf<Language>()
    val codeResponses = mutableListOf<AssignmentCodeResponse>()
    val submissions = mutableListOf<Submission>()

    override suspend fun getAssignment(referenceId: String): Assignment? {
        return assignments.find { it.referenceId == referenceId }
    }

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignments.firstOrNull { it.id == id }
    }

    override suspend fun getLanguages(): List<Language> {
        return languages
    }

    override suspend fun runCode(language: String, code: String): AssignmentCodeResponse {
        val response = codeResponses.first()
        codeResponses.remove(response)
        return response
    }

    override suspend fun testCode(language: String, code: String, tests: String): AssignmentCodeResponse {
        val response = codeResponses.first()
        codeResponses.remove(response)
        return response
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languages.find { it.id == id }
    }

    override suspend fun deleteAssignment(assignment: Assignment): Boolean {
        return assignments.removeAll { it.id == assignment.id }
    }

    override suspend fun getLastStudentSubmissionForAssignment(assignmentID: UUID, studentId: String): Submission? {
        return submissions.filter { it.assignmentId == assignmentID && it.studentId == studentId }
            .maxByOrNull { it.attempt }
    }

    override suspend fun getStudentSubmissionsForAssignment(assignmentID: UUID, studentId: String): List<Submission> {
        return submissions.filter { it.assignmentId == assignmentID && it.studentId == studentId }
    }

    override suspend fun saveSubmission(submission: Submission) {
        submissions.add(submission)
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        assignments.removeIf { it.id == assignment.id }
        return assignments.add(assignment)
    }

    override suspend fun getAssignments(courseId: UUID): List<Assignment> {
        TODO("Not yet implemented")
    }

    override suspend fun getAiFeedback(
        solution: String,
        instructions: String,
        submission: String,
        user: String,
        language: String,
        error: String?
    ): AiResponse {
        TODO("Not yet implemented")
    }
}

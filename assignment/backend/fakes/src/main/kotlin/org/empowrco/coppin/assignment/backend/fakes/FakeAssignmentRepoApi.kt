package org.empowrco.coppin.assignment.backend.fakes

import org.empowrco.coppin.assignment.backend.AssignmentApiRepository
import org.empowrco.coppin.assignment.backend.AssignmentCodeResponse
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Language
import java.util.UUID

class FakeAssignmentRepoApi : AssignmentApiRepository {

    val assignments = mutableListOf<Assignment>()
    val languages = mutableListOf<Language>()
    val codeResponses = mutableListOf<AssignmentCodeResponse>()

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

    override suspend fun deleteAssignment(id: UUID): Boolean {
        return assignments.removeAll { it.id == id }
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languages.find { it.id == id }
    }
}

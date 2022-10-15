package org.empowrco.coppin.assignment.backend.fakes

import org.empowrco.coppin.assignment.backend.AssignmentRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Language
import java.util.UUID

class FakeAssignmentRepo : AssignmentRepository {

    val assignments = mutableListOf<Assignment>()
    val languages = mutableListOf<Language>()

    override suspend fun getAssignment(referenceId: String): Assignment? {
        return assignments.find { it.referenceId == referenceId }
    }

    override suspend fun createAssignment(assignment: Assignment) {
        assignments.add(assignment)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languages.find { it.id == id }
    }
}

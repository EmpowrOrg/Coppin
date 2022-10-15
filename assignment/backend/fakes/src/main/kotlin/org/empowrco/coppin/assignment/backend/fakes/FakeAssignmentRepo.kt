package org.empowrco.coppin.assignment.backend.fakes

import org.empowrco.coppin.assignment.backend.AssignmentRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.ExecutorResponse

class FakeAssignmentRepo : AssignmentRepository {

    val assignments = mutableListOf<Assignment>()
    val executorResponses = mutableListOf<ExecutorResponse>()

    override suspend fun executeCode(language: String, code: String): ExecutorResponse {
        return executorResponses.last()
    }

    override suspend fun getAssignment(referenceId: String): Assignment? {
        return assignments.find { it.referenceId == referenceId }
    }

    override suspend fun createAssignment(assignment: Assignment): Assignment? {
        assignments.add(assignment)
        return assignment
    }
}

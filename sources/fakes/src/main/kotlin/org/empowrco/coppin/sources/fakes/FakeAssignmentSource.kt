package org.empowrco.coppin.sources.fakes

import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.sources.AssignmentSource
import java.util.UUID

class FakeAssignmentSource : AssignmentSource {

    val assignments = mutableListOf<Assignment>()

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignments.find { it.id == id }
    }

    override suspend fun getAssignmentByReferenceId(id: String): Assignment? {
        return assignments.find { it.referenceId == id }
    }

    override suspend fun createAssignment(assignment: Assignment) {
        assignments.add(assignment)
    }

    override suspend fun deleteAssignment(id: UUID): Boolean {
        return assignments.removeIf { it.id == id }
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        val result = assignments.removeIf {
            assignment.id == it.id
        }
        if (result) {
            assignments.add(assignment)
        }
        return result
    }

    override suspend fun getAssignments(): List<Assignment> {
        return assignments
    }
}

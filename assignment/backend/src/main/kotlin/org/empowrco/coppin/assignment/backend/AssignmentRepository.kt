package org.empowrco.coppin.assignment.backend

import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.LanguagesSource
import java.util.UUID

interface AssignmentRepository {
    suspend fun getAssignment(referenceId: String): Assignment?
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun createAssignment(assignment: Assignment)
    suspend fun getLanguage(id: UUID): Language?
}

internal class RealAssignmentRepository(
    private val assignmentSource: AssignmentSource,
    private val languagesSource: LanguagesSource,
) : AssignmentRepository {

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignmentSource.getAssignment(id)
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        return assignmentSource.updateAssignment(assignment)
    }

    override suspend fun getAssignment(referenceId: String): Assignment? {
        return assignmentSource.getAssignmentByReferenceId(referenceId)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languagesSource.getLanguage(id)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        return assignmentSource.createAssignment(assignment)
    }
}

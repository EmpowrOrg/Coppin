package org.empowrco.coppin.sources

import org.empowrco.coppin.db.Assignments
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface AssignmentSource {
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun getAssignmentByReferenceId(id: String): Assignment?
    suspend fun createAssignment(assignment: Assignment)
    suspend fun deleteAssignment(id: UUID): Boolean
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getAssignments(): List<Assignment>
    suspend fun getAssignmentsForCourse(id: UUID): List<Assignment>
}

internal class RealAssignmentSource(
    private val assignmentCodesSource: AssignmentCodesSource,
) : AssignmentSource {
    override suspend fun getAssignment(id: UUID): Assignment? = dbQuery {
        Assignments.select { Assignments.id eq id }.limit(1).map { buildAssigment(it) }.firstOrNull()
    }

    override suspend fun getAssignments(): List<Assignment> = dbQuery {
        Assignments.selectAll().map { buildAssigment(it) }
    }

    override suspend fun getAssignmentsForCourse(id: UUID) = dbQuery {
        Assignments.select { Assignments.courseId eq id }.map { buildAssigment(it) }
    }

    override suspend fun getAssignmentByReferenceId(id: String): Assignment? = dbQuery {
        Assignments.select { Assignments.referenceId eq id }.limit(1).map {
            buildAssigment(it)
        }.firstOrNull()
    }

    private suspend fun buildAssigment(result: ResultRow): Assignment {
        val assignmentId = result[Assignments.id].value
        val starterCodes = assignmentCodesSource.getByAssigment(assignmentId)
        return result.toAssignment(starterCodes)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        dbQuery {
            Assignments.insert {
                it.build(assignment, true)
            }
        }
    }

    override suspend fun deleteAssignment(id: UUID): Boolean {
        val result = dbQuery {
            Assignments.deleteWhere { Assignments.id eq  id }
        }
        assignmentCodesSource.deleteByAssignment(id)
        return result > 0
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        val result = dbQuery {
            Assignments.update({ Assignments.id eq assignment.id }) {
                it.build(assignment, false)
            }
        }
        return result > 0
    }
}

internal fun UpdateBuilder<*>.build(assignment: Assignment, isCreate: Boolean) {
    if (isCreate) {
        this[Assignments.id] = assignment.id
        this[Assignments.createdAt] = assignment.createdAt
    }
    this[Assignments.title] = assignment.title
    this[Assignments.referenceId] = assignment.referenceId
    this[Assignments.successMessage] = assignment.successMessage
    this[Assignments.failureMessage] = assignment.failureMessage
    this[Assignments.instructions] = assignment.instructions
    this[Assignments.totalAttempts] = assignment.totalAttempts
    this[Assignments.lastModifiedAt] = assignment.lastModifiedAt
    this[Assignments.blockId] = assignment.blockId
}

private fun ResultRow.toAssignment(assignmentCodes: List<AssignmentCode>): Assignment {
    val id = this[Assignments.id].value
    return Assignment(
        id = id,
        referenceId = this[Assignments.referenceId],
        assignmentCodes = assignmentCodes,
        title = this[Assignments.title],
        createdAt = this[Assignments.createdAt],
        lastModifiedAt = this[Assignments.lastModifiedAt],
        failureMessage = this[Assignments.failureMessage],
        successMessage = this[Assignments.successMessage],
        instructions = this[Assignments.instructions],
        totalAttempts = this[Assignments.totalAttempts],
        blockId = this[Assignments.blockId]
    )
}


package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Assignments
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface AssignmentSource {
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun getAssignmentByReferenceId(id: String): Assignment?
    suspend fun getAssignmentCountBySubject(id: UUID): Long
    suspend fun createAssignment(assignment: Assignment)
    suspend fun deleteAssignment(assignment: Assignment): Boolean
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getAssignmentsForCourse(id: UUID): List<Assignment>
    suspend fun getAssignmentsForSubject(id: UUID): List<Assignment>
    suspend fun assignmentsWithReferenceStartingWithCount(name: String): Long
}

internal class RealAssignmentSource(
    cache: Cache,
    assignmentCodesSource: AssignmentCodesSource,
    subjectSource: SubjectSource,
) : AssignmentSource {
    private val cache = CacheAssignmentSource(cache)
    private val database = DatabaseAssignmentSource(assignmentCodesSource, subjectSource)
    override suspend fun getAssignment(id: UUID): Assignment? {
        return cache.getAssignment(id) ?: database.getAssignment(id)?.also {
            cache.createAssignment(it)
        }
    }

    override suspend fun getAssignmentByReferenceId(id: String): Assignment? {
        return cache.getAssignmentByReferenceId(id) ?: database.getAssignmentByReferenceId(id)?.also {
            cache.createAssignment(it)
        }
    }

    override suspend fun getAssignmentCountBySubject(id: UUID): Long {
        return database.getAssignmentCountBySubject(id)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        database.createAssignment(assignment)
        cache.createAssignment(assignment)
    }

    override suspend fun deleteAssignment(assignment: Assignment): Boolean {
        cache.deleteAssignment(assignment)
        return database.deleteAssignment(assignment)
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        cache.deleteAssignment(assignment)
        return database.updateAssignment(assignment)
    }

    override suspend fun assignmentsWithReferenceStartingWithCount(name: String): Long {
        return database.assignmentsWithReferenceStartingWithCount(name)
    }

    override suspend fun getAssignmentsForCourse(id: UUID): List<Assignment> {
        return database.getAssignmentsForCourse(id)
    }

    override suspend fun getAssignmentsForSubject(id: UUID): List<Assignment> {
        return database.getAssignmentsForSubject(id)
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheAssignmentSource(private val cache: Cache) : AssignmentSource {

    fun assignmentKey(id: UUID?, referenceId: String?) = "assignment:$id:$referenceId"

    override suspend fun getAssignment(id: UUID): Assignment? {
        return cache.get(assignmentKey(id, null), Assignment::class.serializer())
    }

    override suspend fun getAssignmentByReferenceId(id: String): Assignment? {
        return cache.get(assignmentKey(null, id), Assignment::class.serializer())
    }

    override suspend fun getAssignmentCountBySubject(id: UUID): Long {
        throw NotImplementedError("Use Database")
    }

    override suspend fun createAssignment(assignment: Assignment) {
        cache.set(assignmentKey(assignment.id, null), json.encodeToString(assignment))
        cache.set(assignmentKey(null, assignment.referenceId), json.encodeToString(assignment))
    }

    override suspend fun deleteAssignment(assignment: Assignment): Boolean {
        cache.delete(assignmentKey(assignment.id, null))
        cache.delete(assignmentKey(null, assignment.referenceId))
        return true
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        throw NotImplementedError("Use Database")
    }

    override suspend fun getAssignmentsForCourse(id: UUID): List<Assignment> {
        throw NotImplementedError("Use Database")
    }

    override suspend fun getAssignmentsForSubject(id: UUID): List<Assignment> {
        throw NotImplementedError("Use Database")
    }

    override suspend fun assignmentsWithReferenceStartingWithCount(name: String): Long {
        throw NotImplementedError("Use Database")
    }
}

private class DatabaseAssignmentSource(
    private val assignmentCodesSource: AssignmentCodesSource,
    private val subjectSource: SubjectSource,
) : AssignmentSource {
    override suspend fun getAssignment(id: UUID): Assignment? = dbQuery {
        Assignments.select { (Assignments.id eq id) and (Assignments.archived eq false) }.limit(1)
            .map { buildAssigment(it) }.firstOrNull()
    }

    override suspend fun getAssignmentsForSubject(id: UUID): List<Assignment> = dbQuery {
        Assignments.select { (Assignments.subject eq id) and (Assignments.archived eq false) }
            .map { buildAssigment(it) }
    }

    override suspend fun assignmentsWithReferenceStartingWithCount(name: String): Long = dbQuery {
        Assignments.select { (Assignments.referenceId like "$name%") }.count()
    }

    override suspend fun getAssignmentCountBySubject(id: UUID): Long = dbQuery {
        Assignments.select { Assignments.subject eq id }.count()
    }

    override suspend fun getAssignmentsForCourse(id: UUID) = dbQuery {
        Assignments.select { (Assignments.courseId eq id) and (Assignments.archived eq false) }
            .map { buildAssigment(it) }
    }

    override suspend fun getAssignmentByReferenceId(id: String): Assignment? = dbQuery {
        Assignments.select { (Assignments.referenceId eq id) and (Assignments.archived eq false) }
            .limit(1).map {
            buildAssigment(it)
        }.firstOrNull()
    }

    private suspend fun buildAssigment(result: ResultRow): Assignment {
        val assignmentId = result[Assignments.id].value
        val starterCodes = assignmentCodesSource.getByAssigment(assignmentId)
        val subjectId = result[Assignments.subject].value
        val subject = subjectSource.getSubject(subjectId) ?: throw Exception("Subject not found")
        return result.toAssignment(starterCodes, subject)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        dbQuery {
            Assignments.insert {
                it.build(assignment, true)
            }
        }
    }

    override suspend fun deleteAssignment(assignment: Assignment): Boolean {
        val result = dbQuery {
            Assignments.deleteWhere { Assignments.id eq assignment.id }
        }
        assignmentCodesSource.deleteByAssignment(assignment)
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
    this[Assignments.archived] = assignment.archived
    this[Assignments.points] = assignment.points
    this[Assignments.courseId] = assignment.courseId
    this[Assignments.subject] = assignment.subject.id
}

private fun ResultRow.toAssignment(assignmentCodes: List<AssignmentCode>, subject: Subject): Assignment {
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
        archived = this[Assignments.archived],
        blockId = this[Assignments.blockId],
        courseId = this[Assignments.courseId].value,
        subject = subject,
        points = this[Assignments.points],
    )
}


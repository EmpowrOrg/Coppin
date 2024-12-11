package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.AssignmentCodes
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface AssignmentCodesSource {
    suspend fun create(assignmentCode: AssignmentCode)
    suspend fun getByAssigment(assignmentId: UUID): List<AssignmentCode>
    suspend fun get(id: UUID): AssignmentCode?
    suspend fun delete(assignmentCode: AssignmentCode): Boolean
    suspend fun update(assignmentCode: AssignmentCode): Boolean
    suspend fun deleteByAssignment(assignment: Assignment): Boolean
    suspend fun deprimaryAssignmentCodes(assignment: Assignment)
    suspend fun getCodeCountForLanguage(id: UUID): Long
}

internal class RealAssignmentCodesSource(cache: Cache, languagesSource: LanguagesSource) : AssignmentCodesSource {

    private val cache = CacheAssignmentCodesSource(cache)
    private val database = DatabaseAssignmentCodesSource(languagesSource)
    override suspend fun create(assignmentCode: AssignmentCode) {
        database.create(assignmentCode)
        cache.create(assignmentCode)
    }

    override suspend fun getByAssigment(assignmentId: UUID): List<AssignmentCode> {
        return cache.getByAssigment(assignmentId).ifEmpty {
            database.getByAssigment(assignmentId).also {
                cache.createByAssignment(assignmentId, it)
            }
        }
    }

    override suspend fun get(id: UUID): AssignmentCode? {
        return cache.get(id) ?: database.get(id)?.also {
            cache.create(it)
        }
    }

    override suspend fun delete(assignmentCode: AssignmentCode): Boolean {
        cache.delete(assignmentCode)
        return database.delete(assignmentCode)
    }

    override suspend fun update(assignmentCode: AssignmentCode): Boolean {
        cache.update(assignmentCode)
        return database.update(assignmentCode)
    }

    override suspend fun deleteByAssignment(assignment: Assignment): Boolean {
        cache.deleteByAssignment(assignment)
        return database.deleteByAssignment(assignment)
    }

    override suspend fun deprimaryAssignmentCodes(assignment: Assignment) {
        cache.deprimaryAssignmentCodes(assignment)
        database.deprimaryAssignmentCodes(assignment)
    }

    override suspend fun getCodeCountForLanguage(id: UUID): Long {
        return database.getCodeCountForLanguage(id)
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheAssignmentCodesSource(private val cache: Cache) : AssignmentCodesSource {

    private fun codeKey(id: UUID) = "code:$id"
    private fun codesKey(id: UUID) = "codes:$id"
    override suspend fun create(assignmentCode: AssignmentCode) {
        cache.set(codeKey(assignmentCode.id), json.encodeToString(assignmentCode))
        cache.delete(codesKey(assignmentCode.assignmentId))
    }

    override suspend fun getByAssigment(assignmentId: UUID): List<AssignmentCode> {
        return cache.getList(codesKey(assignmentId), AssignmentCode::class.serializer())
    }

    suspend fun createByAssignment(assignmentId: UUID, codes: List<AssignmentCode>) {
        cache.set(codesKey(assignmentId), json.encodeToString(codes))
    }

    override suspend fun get(id: UUID): AssignmentCode? {
        return cache.get(codeKey(id), AssignmentCode::class.serializer())
    }

    override suspend fun delete(assignmentCode: AssignmentCode): Boolean {
        cache.delete(codeKey(assignmentCode.id))
        cache.delete(codesKey(assignmentCode.assignmentId))
        return true
    }

    override suspend fun update(assignmentCode: AssignmentCode): Boolean {
        cache.delete(codeKey(assignmentCode.id))
        cache.delete(codesKey(assignmentCode.assignmentId))
        return true
    }

    override suspend fun deleteByAssignment(assignment: Assignment): Boolean {
        cache.delete(codesKey(assignment.id))
        assignment.assignmentCodes.forEach {
            cache.delete(codeKey(it.id))
        }
        return true
    }

    override suspend fun deprimaryAssignmentCodes(assignment: Assignment) {
        deleteByAssignment(assignment)
    }

    override suspend fun getCodeCountForLanguage(id: UUID): Long {
        throw NotImplementedError("Use Database")
    }
}

private class DatabaseAssignmentCodesSource(private val languagesSource: LanguagesSource) : AssignmentCodesSource {
    override suspend fun create(assignmentCode: AssignmentCode) = dbQuery {
        AssignmentCodes.insert {
            it.build(assignmentCode)
        }
        Unit
    }

    override suspend fun getByAssigment(assignmentId: UUID): List<AssignmentCode> = dbQuery {
        AssignmentCodes.selectAll().where { AssignmentCodes.assignment eq assignmentId }.map {
            val languageId = it[AssignmentCodes.language].value
            val language = languagesSource.getLanguage(languageId)!!
            it.toStarterCode(language)
        }
    }

    override suspend fun get(id: UUID): AssignmentCode? = dbQuery {
        AssignmentCodes.selectAll().where { AssignmentCodes.id eq id }.map {
            val languageId = it[AssignmentCodes.language].value
            val language = languagesSource.getLanguage(languageId)!!
            it.toStarterCode(language)
        }.firstOrNull()
    }

    override suspend fun getCodeCountForLanguage(id: UUID): Long = dbQuery {
        AssignmentCodes.selectAll().where { AssignmentCodes.language eq id }.count()
    }

    override suspend fun delete(assignmentCode: AssignmentCode): Boolean = dbQuery {
        AssignmentCodes.deleteWhere { AssignmentCodes.id eq assignmentCode.id } > 0
    }

    override suspend fun deleteByAssignment(assignment: Assignment): Boolean = dbQuery {
        AssignmentCodes.deleteWhere { AssignmentCodes.assignment eq assignment.id } > 0
    }

    override suspend fun update(assignmentCode: AssignmentCode): Boolean = dbQuery {
        AssignmentCodes.update({ AssignmentCodes.id eq assignmentCode.id }) {
            it.build(assignmentCode)
        } > 0
    }

    override suspend fun deprimaryAssignmentCodes(assignment: Assignment) = dbQuery {
        AssignmentCodes.update({ AssignmentCodes.assignment eq assignment.id }) {
            it[primary] = false
        }
        Unit
    }
}

private fun ResultRow.toStarterCode(language: Language): AssignmentCode {
    return AssignmentCode(
        id = this[AssignmentCodes.id].value,
        assignmentId = this[AssignmentCodes.assignment].value,
        language = language,
        starterCode = this[AssignmentCodes.starterCode],
        solutionCode = this[AssignmentCodes.solutionCode],
        primary = this[AssignmentCodes.primary],
        unitTest = this[AssignmentCodes.unitTest],
        injectable = this[AssignmentCodes.injectable],
        solutionVisibility = this[AssignmentCodes.solutionVisibility],
        createdAt = this[AssignmentCodes.createdAt],
        lastModifiedAt = this[AssignmentCodes.lastModifiedAt],
    )
}

private fun UpdateBuilder<*>.build(assignmentCode: AssignmentCode) {
    this[AssignmentCodes.id] = assignmentCode.id
    this[AssignmentCodes.assignment] = assignmentCode.assignmentId
    this[AssignmentCodes.starterCode] = assignmentCode.starterCode
    this[AssignmentCodes.solutionCode] = assignmentCode.solutionCode
    this[AssignmentCodes.unitTest] = assignmentCode.unitTest
    this[AssignmentCodes.solutionVisibility] = assignmentCode.solutionVisibility
    this[AssignmentCodes.language] = assignmentCode.language.id
    this[AssignmentCodes.injectable] = assignmentCode.injectable
    this[AssignmentCodes.createdAt] = assignmentCode.createdAt
    this[AssignmentCodes.lastModifiedAt] = assignmentCode.lastModifiedAt
    this[AssignmentCodes.primary] = assignmentCode.primary
}

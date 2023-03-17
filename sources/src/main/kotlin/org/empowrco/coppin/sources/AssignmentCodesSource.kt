package org.empowrco.coppin.sources

import org.empowrco.coppin.db.AssignmentCodes
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Language
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface AssignmentCodesSource {
    suspend fun create(assignmentCodes: List<AssignmentCode>)
    suspend fun getByAssigment(assignmentId: UUID): List<AssignmentCode>
    suspend fun get(id: UUID): AssignmentCode?
    suspend fun delete(id: UUID): Boolean
    suspend fun update(assignmentCode: AssignmentCode): Boolean
    suspend fun deleteByAssignment(assignmentId: UUID): Boolean
    suspend fun deprimaryAssignmentCodes(assignmentId: UUID)
    suspend fun getCodeCountForLanguage(id: UUID): Long
}

internal class RealAssignmentCodesSource(private val languagesSource: LanguagesSource) : AssignmentCodesSource {
    override suspend fun create(assignmentCodes: List<org.empowrco.coppin.models.AssignmentCode>) = dbQuery {
        AssignmentCodes.batchInsert(assignmentCodes) { this.build(it) }
        Unit
    }

    override suspend fun getByAssigment(assignmentId: UUID): List<org.empowrco.coppin.models.AssignmentCode> = dbQuery {
        AssignmentCodes.select { AssignmentCodes.assignment eq assignmentId }.map {
            val languageId = it[AssignmentCodes.language].value
            val language = languagesSource.getLanguage(languageId)!!
            it.toStarterCode(language)
        }
    }

    override suspend fun get(id: UUID): AssignmentCode? = dbQuery {
        AssignmentCodes.select { AssignmentCodes.id eq id }.map {
            val languageId = it[AssignmentCodes.language].value
            val language = languagesSource.getLanguage(languageId)!!
            it.toStarterCode(language)
        }.firstOrNull()
    }

    override suspend fun getCodeCountForLanguage(id: UUID): Long = dbQuery {
        AssignmentCodes.select { AssignmentCodes.language eq id }.count()
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        AssignmentCodes.deleteWhere { AssignmentCodes.id eq id } > 0
    }

    override suspend fun deleteByAssignment(assignmentId: UUID): Boolean = dbQuery {
        AssignmentCodes.deleteWhere { AssignmentCodes.assignment eq assignmentId } > 0
    }

    override suspend fun update(assignmentCode: org.empowrco.coppin.models.AssignmentCode): Boolean = dbQuery {
        AssignmentCodes.update({ AssignmentCodes.id eq assignmentCode.id }) {
            it.build(assignmentCode)
        } > 0
    }

    override suspend fun deprimaryAssignmentCodes(assignmentId: UUID) = dbQuery {
        AssignmentCodes.update({ AssignmentCodes.assignment eq assignmentId }) {
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
    this[AssignmentCodes.language] = assignmentCode.language.id
    this[AssignmentCodes.injectable] = assignmentCode.injectable
    this[AssignmentCodes.createdAt] = assignmentCode.createdAt
    this[AssignmentCodes.lastModifiedAt] = assignmentCode.lastModifiedAt
    this[AssignmentCodes.primary] = assignmentCode.primary
}

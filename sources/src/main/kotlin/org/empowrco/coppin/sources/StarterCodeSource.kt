package org.empowrco.coppin.sources

import org.empowrco.coppin.db.StarterCodes
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.StarterCode
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface StarterCodeSource {
    suspend fun create(starterCodes: List<StarterCode>)
    suspend fun getByAssigment(assignmentId: UUID): List<StarterCode>
    suspend fun delete(id: UUID): Boolean
    suspend fun update(starterCode: StarterCode): Boolean
    suspend fun deleteByAssignment(assignmentId: UUID): Boolean
}

internal class RealStarterCodeSource(private val languagesSource: LanguagesSource): StarterCodeSource {
    override suspend fun create(starterCodes: List<StarterCode>) = dbQuery {
        StarterCodes.batchInsert(starterCodes) { this.build(it) }
        Unit
    }

    override suspend fun getByAssigment(assignmentId: UUID): List<StarterCode> = dbQuery {
        StarterCodes.select { StarterCodes.assignment eq assignmentId }.map {
            val languageId = it[StarterCodes.language].value
            val language = languagesSource.getLanguage(languageId)!!
            it.toStarterCode(language)
        }
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        StarterCodes.deleteWhere { StarterCodes.id eq id } > 0
    }

    override suspend fun deleteByAssignment(assignmentId: UUID): Boolean = dbQuery {
        StarterCodes.deleteWhere { StarterCodes.assignment eq assignmentId } > 0
    }

    override suspend fun update(starterCode: StarterCode): Boolean = dbQuery {
        StarterCodes.update({ StarterCodes.id eq starterCode.id }) {
            it.build(starterCode)
        } > 0
    }
}

private fun ResultRow.toStarterCode(language: Language): StarterCode {
    return StarterCode(
        id = this[StarterCodes.id].value,
        assignmentId = this[StarterCodes.assignment].value,
        language = language,
        code = this[StarterCodes.code],
        primary = this[StarterCodes.primary],
        createdAt = this[StarterCodes.createdAt],
        lastModifiedAt = this[StarterCodes.lastModifiedAt],
    )
}

private fun UpdateBuilder<*>.build(starterCode: StarterCode) {
    this[StarterCodes.id] = starterCode.id
    this[StarterCodes.assignment] = starterCode.assignmentId
    this[StarterCodes.code] = starterCode.code
    this[StarterCodes.language] = starterCode.language.id
    this[StarterCodes.createdAt] = starterCode.createdAt
    this[StarterCodes.lastModifiedAt] = starterCode.lastModifiedAt
    this[StarterCodes.primary] = starterCode.primary
}

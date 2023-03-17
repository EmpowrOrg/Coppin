package org.empowrco.coppin.sources

import org.empowrco.coppin.db.Languages
import org.empowrco.coppin.models.Language
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface LanguagesSource {
    suspend fun create(language: Language)
    suspend fun getLanguage(id: UUID): Language?
    suspend fun getLanguageByMime(mime: String): Language?
    suspend fun deleteLanguage(id: UUID): Boolean
    suspend fun updateLanguage(language: Language): Boolean
    suspend fun getLanguages(): List<Language>
}

internal class RealLanguageSource: LanguagesSource {
    override suspend fun create(language: Language) = dbQuery {
        Languages.insert {
            it.build(language, false)
        }
        Unit
    }


    override suspend fun getLanguage(id: UUID): Language? = dbQuery {
        Languages.select { Languages.id eq id }.map { it.toLanguage() }.firstOrNull()
    }
    override suspend fun getLanguageByMime(mime: String): Language? = dbQuery {
        Languages.select { Languages.mime eq mime }.map { it.toLanguage() }.firstOrNull()
    }

    override suspend fun deleteLanguage(id: UUID): Boolean = dbQuery {
        Languages.deleteWhere { Languages.id eq id } > 0
    }

    override suspend fun updateLanguage(language: Language): Boolean = dbQuery {
        Languages.update({Languages.id eq language.id}) {
            it.build(language, true)
        } > 0
    }

    override suspend fun getLanguages(): List<Language> = dbQuery {
        Languages.selectAll().map { it.toLanguage() }
    }
}

private fun UpdateBuilder<*>.build(language: Language, isUpdate: Boolean) {
    this[Languages.id] = language.id
    this[Languages.mime] = language.mime
    this[Languages.name] = language.name
    this[Languages.url] = language.url
    this[Languages.unitTestRegex] = language.unitTestRegex
    if (!isUpdate) {
        this[Languages.createdAt] = language.createdAt
    }
    this[Languages.lastModifiedAt] = language.lastModifiedAt
}

private fun ResultRow.toLanguage(): Language {
    return Language(
        id = this[Languages.id].value,
        name = this[Languages.name],
        mime = this[Languages.mime],
        url = this[Languages.url],
        unitTestRegex = this[Languages.unitTestRegex],
        lastModifiedAt = this[Languages.lastModifiedAt],
        createdAt = this[Languages.createdAt],
    )
}

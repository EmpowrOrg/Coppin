package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Languages
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.utils.serialization.json
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
    suspend fun deleteLanguage(language: Language): Boolean
    suspend fun updateLanguage(language: Language): Boolean
    suspend fun getLanguages(): List<Language>
}

internal class RealLanguagesSource(cache: Cache) : LanguagesSource {

    private val cache = CacheLanguagesSource(cache)
    private val database = DatabaseLanguagesSource()
    override suspend fun create(language: Language) {
        database.create(language)
        cache.create(language)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return cache.getLanguage(id) ?: database.getLanguage(id)?.also {
            cache.create(it)
        }
    }

    override suspend fun getLanguageByMime(mime: String): Language? {
        return cache.getLanguageByMime(mime) ?: database.getLanguageByMime(mime)?.also {
            cache.create(it)
        }
    }

    override suspend fun deleteLanguage(language: Language): Boolean {
        cache.deleteLanguage(language)
        return database.deleteLanguage(language)
    }

    override suspend fun updateLanguage(language: Language): Boolean {
        cache.updateLanguage(language)
        return database.updateLanguage(language)
    }

    override suspend fun getLanguages(): List<Language> {
        return cache.getLanguages().ifEmpty {
            return database.getLanguages().also {
                cache.saveLanguages(it)
            }
        }
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheLanguagesSource(private val cache: Cache) : LanguagesSource {

    private fun languageKey(id: UUID?, mime: String?) = "language:$id:$mime"
    private fun languagesKey() = "languages"
    override suspend fun create(language: Language) {
        cache.set(languageKey(language.id, null), json.encodeToString(language))
        cache.set(languageKey(null, language.mime), json.encodeToString(language))
        cache.delete(languagesKey())
    }


    override suspend fun getLanguage(id: UUID): Language? {
        return cache.get(languageKey(id, null), Language::class.serializer())
    }

    override suspend fun getLanguageByMime(mime: String): Language? {
        return cache.get(languageKey(null, mime), Language::class.serializer())
    }

    override suspend fun deleteLanguage(language: Language): Boolean {
        cache.delete(languageKey(language.id, null))
        cache.delete(languageKey(null, language.mime))
        cache.delete(languagesKey())
        return true
    }

    override suspend fun updateLanguage(language: Language): Boolean {
        cache.delete(languageKey(language.id, null))
        cache.delete(languageKey(null, language.mime))
        cache.delete(languagesKey())
        return true
    }

    override suspend fun getLanguages(): List<Language> {
        return cache.getList(languagesKey(), Language::class.serializer())
    }

    suspend fun saveLanguages(languages: List<Language>) {
        cache.set(languagesKey(), json.encodeToString(languages))
    }
}

private class DatabaseLanguagesSource : LanguagesSource {
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

    override suspend fun deleteLanguage(language: Language): Boolean = dbQuery {
        Languages.deleteWhere { Languages.id eq language.id } > 0
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

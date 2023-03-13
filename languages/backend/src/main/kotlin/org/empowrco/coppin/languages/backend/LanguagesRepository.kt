package org.empowrco.coppin.languages.backend

import org.empowrco.coppin.models.Language
import org.empowrco.coppin.sources.AssignmentCodesSource
import org.empowrco.coppin.sources.LanguagesSource
import java.util.UUID

interface LanguagesRepository {
    suspend fun createLanguage(language: Language)
    suspend fun deleteLanguage(id: UUID): Boolean
    suspend fun updateLanguage(language: Language): Boolean
    suspend fun getLanguage(id: UUID): Language?
    suspend fun getLanguages(): List<Language>
    suspend fun getCodeCountForLanguage(id: UUID): Long
}

internal class RealLanguagesRepository(
    private val languagesSource: LanguagesSource,
    private val codeSource: AssignmentCodesSource,
) : LanguagesRepository {
    override suspend fun createLanguage(language: Language) {
        languagesSource.create(language)
    }

    override suspend fun deleteLanguage(id: UUID): Boolean {
        return languagesSource.deleteLanguage(id)
    }

    override suspend fun updateLanguage(language: Language): Boolean {
        return languagesSource.updateLanguage(language)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languagesSource.getLanguage(id)
    }

    override suspend fun getLanguages(): List<Language> {
        return languagesSource.getLanguages()
    }

    override suspend fun getCodeCountForLanguage(id: UUID): Long {
        return codeSource.getCodeCountForLanguage(id)
    }
}

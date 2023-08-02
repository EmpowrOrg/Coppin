package org.empowrco.coppin.languages.backend

import org.empowrco.coppin.models.Language
import org.empowrco.coppin.sources.AssignmentCodesSource
import org.empowrco.coppin.sources.LanguagesSource
import java.util.UUID

interface LanguagesApiRepository {
    suspend fun deleteLanguage(language: Language): Boolean
    suspend fun getCodeCountForLanguage(id: UUID): Long
    suspend fun getLanguage(id: UUID): Language?
}

internal class RealLanguagesApiRepository(
    private val languagesSource: LanguagesSource,
    private val codeSource: AssignmentCodesSource,
) : LanguagesApiRepository {

    override suspend fun deleteLanguage(language: Language): Boolean {
        return languagesSource.deleteLanguage(language)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languagesSource.getLanguage(id)
    }

    override suspend fun getCodeCountForLanguage(id: UUID): Long {
        return codeSource.getCodeCountForLanguage(id)
    }
}

package org.empowrco.coppin.languages.backend

import org.empowrco.coppin.sources.AssignmentCodesSource
import org.empowrco.coppin.sources.LanguagesSource
import java.util.UUID

interface LanguagesApiRepository {
    suspend fun deleteLanguage(id: UUID): Boolean
    suspend fun getCodeCountForLanguage(id: UUID): Long
}

internal class RealLanguagesApiRepository(
    private val languagesSource: LanguagesSource,
    private val codeSource: AssignmentCodesSource,
) : LanguagesApiRepository {

    override suspend fun deleteLanguage(id: UUID): Boolean {
        return languagesSource.deleteLanguage(id)
    }

    override suspend fun getCodeCountForLanguage(id: UUID): Long {
        return codeSource.getCodeCountForLanguage(id)
    }
}

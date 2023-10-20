package org.empowrco.coppin.sources.fakes

import org.empowrco.coppin.models.Language
import org.empowrco.coppin.sources.LanguagesSource
import java.util.UUID

class FakeLanguagesSource : LanguagesSource {
    val languages = mutableListOf<Language>()
    override suspend fun create(language: Language) {
        languages.add(language)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languages.find { it.id == id }
    }

    override suspend fun getLanguageByMime(mime: String): Language? {
        return languages.find { it.mime == mime }
    }

    override suspend fun deleteLanguage(language: Language): Boolean {
        return languages.removeIf { it.id == language.id }
    }

    override suspend fun updateLanguage(language: Language): Boolean {
        val languageIndex = languages.indexOfFirst { it.id == language.id }
        if (languageIndex < 0) {
            return false
        }
        languages[languageIndex] = language
        return true
    }

    override suspend fun getLanguages(): List<Language> {
        return languages
    }
}

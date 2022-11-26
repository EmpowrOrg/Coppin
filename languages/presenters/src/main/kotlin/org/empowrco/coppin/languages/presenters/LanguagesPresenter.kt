package org.empowrco.coppin.languages.presenters

import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.languages.backend.LanguagesRepository
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.portal.LanguageItem
import org.empowrco.coppin.models.portal.LanguageListItem
import org.empowrco.coppin.utils.InvalidUuidException
import org.empowrco.coppin.utils.now
import java.util.UUID

interface LanguagesPresenter {
    suspend fun getLanguages(): GetLanguagesResponse
    suspend fun getLanguage(id: String?): LanguageItem?
    suspend fun saveLanguage(request: CreateLanguageRequest)
    suspend fun updateLanguage(request: UpdateLanguageRequest)
    suspend fun deleteLanguage(id: String)
}

internal class RealLanguagesPresenter(private val repo: LanguagesRepository): LanguagesPresenter {
    override suspend fun getLanguages(): GetLanguagesResponse {
        return GetLanguagesResponse(
            languages = repo.getLanguages().map {
                LanguageListItem(
                    id = it.id.toString(),
                    mime = it.mime,
                    name = it.name,
                )
            }
        )
    }

    override suspend fun getLanguage(id: String?): LanguageItem? {
        id ?: return null
        val uuid = UUID.fromString(id) ?: throw InvalidUuidException("id")
        return repo.getLanguage(uuid)?.let {
            LanguageItem(
                id = it.id.toString(),
                name = it.name,
                url = it.url,
                mime = it.mime,
                supportsUnitTests = it.supportsUnitTests,
            )
        }
    }

    override suspend fun saveLanguage(request: CreateLanguageRequest) {
        val currentTime = LocalDateTime.now()
        val supportsUnitTests = request.supportsUnitTests == "on"
        val language = Language(
            id = UUID.randomUUID(),
            url = request.url,
            mime = request.mime,
            name = request.name,
            supportsUnitTests = supportsUnitTests,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        repo.createLanguage(language)
    }

    override suspend fun updateLanguage(request: UpdateLanguageRequest) {
        val currentTime = LocalDateTime.now()
        val uuid = UUID.fromString(request.id) ?: throw InvalidUuidException("id")
        val language = repo.getLanguage(uuid) ?: throw NotFoundException()
        val supportsUnitTests = request.supportsUnitTests == "on"
        val updatedLanguage = language.copy(
            url = request.url,
            mime = request.mime,
            name = request.name,
            lastModifiedAt = currentTime,
            supportsUnitTests = supportsUnitTests,
        )
        repo.updateLanguage(updatedLanguage)
    }

    override suspend fun deleteLanguage(id: String) {
        repo.deleteLanguage(UUID.fromString(id))
    }
}

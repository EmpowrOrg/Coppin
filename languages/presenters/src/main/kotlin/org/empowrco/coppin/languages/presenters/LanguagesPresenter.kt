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
    suspend fun getLanguage(request: GetLanguageRequest): LanguageItem?
    suspend fun saveLanguage(request: CreateLanguageRequest)
    suspend fun updateLanguage(request: UpdateLanguageRequest)
    suspend fun deleteLanguage(request: DeleteLanguageRequest)
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

    override suspend fun getLanguage(request: GetLanguageRequest): LanguageItem? {
        request.id ?: return null
        val uuid = UUID.fromString(request.id) ?: throw InvalidUuidException("id")
        return repo.getLanguage(uuid)?.let {
            LanguageItem(
                id = it.id.toString(),
                name = it.name,
                url = it.url,
                mime = it.mime,
            )
        }
    }

    override suspend fun saveLanguage(request: CreateLanguageRequest) {
        val currentTime = LocalDateTime.now()
        val language = Language(
            id = UUID.randomUUID(),
            url = request.url,
            mime = request.mime,
            name = request.name,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        repo.createLanguage(language)
    }

    override suspend fun updateLanguage(request: UpdateLanguageRequest) {
        val currentTime = LocalDateTime.now()
        val uuid = UUID.fromString(request.id) ?: throw InvalidUuidException("id")
        val language = repo.getLanguage(uuid) ?: throw NotFoundException()
        val updatedLanguage = language.copy(
            url = request.url,
            mime = request.mime,
            name = request.name,
            lastModifiedAt = currentTime,
        )
        repo.updateLanguage(updatedLanguage)
    }

    override suspend fun deleteLanguage(request: DeleteLanguageRequest) {
        repo.deleteLanguage(UUID.fromString(request.id))
    }
}

package org.empowrco.coppin.languages.presenters

import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.languages.backend.LanguagesRepository
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.portal.LanguageListItem
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.nonEmpty
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

interface LanguagesPresenter {
    suspend fun getLanguages(): Result<GetLanguagesResponse>
    suspend fun getLanguage(request: GetLanguageRequest): Result<GetLanguageResponse>
    suspend fun upsertLanguage(request: UpsertLanguageRequest): Result<UpsertLanguageResponse>
    suspend fun deleteLanguage(request: DeleteLanguageRequest): Result<DeleteLanguageResponse>
}

internal class RealLanguagesPresenter(private val repo: LanguagesRepository) : LanguagesPresenter {
    override suspend fun getLanguages(): Result<GetLanguagesResponse> {
        return GetLanguagesResponse(
            languages = repo.getLanguages().map {
                LanguageListItem(
                    id = it.id.toString(),
                    mime = it.mime,
                    name = it.name,
                )
            }
        ).toResult()
    }

    override suspend fun getLanguage(request: GetLanguageRequest): Result<GetLanguageResponse> {
        request.id ?: return GetLanguageResponse(null, null, null, null).toResult()
        val uuid = request.id.toUuid() ?: return failure("Invalid id")
        val language = repo.getLanguage(uuid) ?: return failure("No language found")
        return GetLanguageResponse(
            id = language.id.toString(),
            name = language.name,
            mime = language.mime,
            url = language.url
        ).toResult()
    }

    override suspend fun upsertLanguage(request: UpsertLanguageRequest): Result<UpsertLanguageResponse> {
        val currentTime = LocalDateTime.now()
        if (request.id.nonEmpty() == null) {
            val language = Language(
                id = UUID.randomUUID(),
                url = request.url,
                mime = request.mime,
                name = request.name,
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
            repo.createLanguage(language)
            return UpsertLanguageResponse.toResult()
        }
        val uuid = request.id?.toUuid() ?: return failure("Invalid id")
        val language = repo.getLanguage(uuid) ?: return failure("Language not found")
        val updatedLanguage = language.copy(
            url = request.url,
            mime = request.mime,
            name = request.name,
            lastModifiedAt = currentTime,
        )
        repo.updateLanguage(updatedLanguage)
        return UpsertLanguageResponse.toResult()
    }

    override suspend fun deleteLanguage(request: DeleteLanguageRequest): Result<DeleteLanguageResponse> {
        val uuid = request.id.toUuid() ?: return failure("Invalid id")
        val result = repo.deleteLanguage(uuid)
        if (!result) {
            return failure("Unknown error")
        }
        return DeleteLanguageResponse(uuid.toString()).toResult()
    }
}

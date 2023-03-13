package org.empowrco.coppin.languages.presenters

import io.ktor.server.plugins.BadRequestException
import org.empowrco.coppin.languages.backend.LanguagesApiRepository
import org.empowrco.coppin.utils.InvalidUuidException
import org.empowrco.coppin.utils.UnknownException
import org.empowrco.coppin.utils.toUuid

interface LanguagesApiPresenter {
    suspend fun deleteLanguage(request: DeleteLanguageRequest): DeleteLanguageResponse
}

internal class RealLanguagesApiPresenter(
    private val repo: LanguagesApiRepository,
) : LanguagesApiPresenter {
    override suspend fun deleteLanguage(request: DeleteLanguageRequest): DeleteLanguageResponse {
        val uuid = request.id.toUuid() ?: throw InvalidUuidException("id")
        val codeCount = repo.getCodeCountForLanguage(uuid)
        if (codeCount > 0) {
            throw BadRequestException("Cannot delete language. Language is associated with Assignments")
        }
        val result = repo.deleteLanguage(uuid)
        if (!result) {
            throw UnknownException
        }
        return DeleteLanguageResponse(uuid.toString())
    }
}

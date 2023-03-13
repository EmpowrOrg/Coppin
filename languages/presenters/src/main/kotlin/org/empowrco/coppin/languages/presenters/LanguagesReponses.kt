package org.empowrco.coppin.languages.presenters

import org.empowrco.coppin.models.portal.LanguageListItem

data class GetLanguagesResponse(val languages: List<LanguageListItem>)

data class GetLanguageResponse(
    val id: String?,
    val name: String?,
    val url: String?,
    val mime: String?,
)

data class DeleteLanguageResponse(val id: String)
object UpsertLanguageResponse

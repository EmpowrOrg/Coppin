package org.empowrco.coppin.languages.presenters

import kotlinx.serialization.Serializable


data class GetLanguagesResponse(val languages: List<Language>, val languagesCount: Int) {
    data class Language(
        val id: String,
        val name: String,
        val mime: String,
        val lastModifiedDate: String,
        val url: String,
    )
}

data class GetLanguageResponse(
    val id: String?,
    val name: String?,
    val url: String?,
    val mime: String?,
    val unitTestRegex: String?,
)

@Serializable
data class DeleteLanguageResponse(val id: String)
object UpsertLanguageResponse

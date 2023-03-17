package org.empowrco.coppin.languages.presenters

data class GetLanguageRequest(
    val id: String?,
)

data class UpsertLanguageRequest(
    val id: String?,
    val url: String,
    val mime: String,
    val name: String,
    val unitTestRegex: String,
)

data class DeleteLanguageRequest(
    val id: String,
)

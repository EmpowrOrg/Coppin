package org.empowrco.coppin.languages.presenters

import kotlinx.serialization.Serializable

@Serializable
data class CreateLanguageRequest(
    val url: String,
    val mime: String,
    val name: String,
)

@Serializable
data class UpdateLanguageRequest(
    val id: String,
    val url: String,
    val mime: String,
    val name: String,
)

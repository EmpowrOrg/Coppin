package org.empowrco.coppin.languages.presenters

import kotlinx.serialization.Serializable
import org.empowrco.coppin.models.Language.Framework.Command
import java.util.*

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

data class GetFrameworkRequest(
    val id: String,
)

data class UpsertFrameworkRequest(
    val id: String,
    val name: String,
    val version: String,
    val commands: List<Command>,
)

data class DeleteFrameworkRequest(
    val id: String,
)

data class CreateFrameworkRequest(
    val name: String,
    val version: String,
    val commands: List<Command>,
)
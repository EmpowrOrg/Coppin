package org.empowrco.coppin.models.portal

import kotlinx.serialization.Serializable

@Serializable
data class AssignmentCodeItem(
    val id: String,
    val starterCode: String,
    val solutionCode: String,
    val assignmentId: String,
    val unitTest: String?,
    val primary: Boolean,
    val language: Language,
    val languages: List<Language>,
) {
    @Serializable
    data class Language(
        val name: String,
        val id: String,
        val url: String,
        val mime: String,
    )
}


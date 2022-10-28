package org.empowrco.coppin.models.portal

data class CodeListItem(
    val id: String,
    val assignmentId: String,
    val language: String,
    val primary: String,
    val hasSolution: String,
    val hasStarter: String,
)

package org.empowrco.coppin.models.portal

data class AssignmentItem(
    val id: String,
    val referenceId: String,
    val instructions: String,
    val successMessage: String,
    val failureMessage: String,
    val attempts: Int,
    val title: String,
)

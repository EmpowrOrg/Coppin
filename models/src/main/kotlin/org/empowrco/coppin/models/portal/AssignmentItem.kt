package org.empowrco.coppin.models.portal

import org.empowrco.coppin.models.Assignment

data class AssignmentItem(
    val id: String,
    val referenceId: String,
    val gradingType: Assignment.GradingType,
    val instructions: String,
    val successMessage: String,
    val failureMessage: String,
    val attempts: Int,
    val title: String,
    val expectedOutput: String?,
)

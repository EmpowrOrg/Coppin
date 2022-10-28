package org.empowrco.coppin.models.portal

data class FeedbackListItem(
    val id: String,
    val feedback: String,
    val attempt: String,
    val regex: String,
    val assignmentId: String,
)

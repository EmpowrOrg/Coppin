package org.empowrco.coppin.models.portal

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackItem(
    val feedback: String,
    val id: String,
    val attempt: Int,
    val regex: String,
    val assignmentId: String,
)

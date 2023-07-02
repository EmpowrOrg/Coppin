package org.empowrco.coppin.models

import java.util.UUID

data class Submission(
    val id: UUID,
    val assignmentID: UUID,
    val correct: Boolean,
    val code: String,
    val attempt: Int,
    val studentEmail: String,
    val studentId: String,
)

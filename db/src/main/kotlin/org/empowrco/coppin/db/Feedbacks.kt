package org.empowrco.coppin.db

import org.jetbrains.exposed.sql.ReferenceOption

object Feedbacks : BaseTable() {
    val regexMatcher = text("regex_matcher")
    val feedback = text("feedback")
    val attempt = integer("attempt")
    val assignmentId = reference("assignment", Assignments.id, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex(assignmentId, attempt, regexMatcher)
    }
}

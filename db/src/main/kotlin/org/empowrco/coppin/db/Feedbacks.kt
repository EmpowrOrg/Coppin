package org.empowrco.coppin.db

object Feedbacks : BaseTable() {
    val regexMatcher = text("regex_matcher")
    val feedback = text("feedback")
    val attempt = integer("attempt")
    val assignmentId = reference("assignment", Assignments.id)

    init {
        uniqueIndex(assignmentId, attempt, regexMatcher)
    }
}

package org.empowrco.coppin.db

object Submissions : BaseTable() {
    val assignment = reference("assignment", Assignments.id)
    val correct = bool("correct")
    val code = text("code")
    val attempt = integer("attempt")
    val studentId = text("student_id")

    init {
        uniqueIndex(assignment, attempt, studentId)
    }
}

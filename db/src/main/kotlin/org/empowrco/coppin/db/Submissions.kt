package org.empowrco.coppin.db

object Submissions : BaseTable() {
    val assignment = reference("assignment", Assignments.id)
    val language = reference("language", Languages.id)
    val correct = bool("correct")
    val code = text("code")
    val attempt = integer("attempt")
    val studentId = text("student_id")
    val feedback = text("feedback")
    init {
        uniqueIndex(assignment, attempt, studentId)
    }
}

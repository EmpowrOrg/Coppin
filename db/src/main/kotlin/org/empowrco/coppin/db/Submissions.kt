package org.empowrco.coppin.db

import org.jetbrains.exposed.dao.id.UUIDTable

object Submissions : UUIDTable() {
    val assignment = reference("assignment", Assignments.id)
    val correct = bool("correct")
    val code = text("code")
    val attempt = integer("attempt")
    val studentEmail = varchar("student_email", ColumnConfig.username)
    val studentId = text("student_id")

    init {
        uniqueIndex(assignment, attempt, studentId)
    }
}

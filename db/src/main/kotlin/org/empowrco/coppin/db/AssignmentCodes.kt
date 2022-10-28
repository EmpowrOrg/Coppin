package org.empowrco.coppin.db

import org.jetbrains.exposed.sql.ReferenceOption

object AssignmentCodes : BaseTable() {
    val assignment = reference("assignment", Assignments.id, onDelete = ReferenceOption.CASCADE)
    val language = reference("language", Languages.id, onDelete = ReferenceOption.CASCADE)
    val starterCode = text("starter_code")
    val solutionCode = text("solution_code")
    val primary = bool("primary")

    init {
        uniqueIndex(assignment, language)
    }
}

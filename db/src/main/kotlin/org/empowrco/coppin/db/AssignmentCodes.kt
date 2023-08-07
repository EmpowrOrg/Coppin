package org.empowrco.coppin.db

import org.empowrco.coppin.models.AssignmentCode
import org.jetbrains.exposed.sql.ReferenceOption

object AssignmentCodes : BaseTable() {
    val assignment = reference("assignment", Assignments.id, onDelete = ReferenceOption.CASCADE)
    val language = reference("language", Languages.id)
    val starterCode = text("starter_code")
    val solutionCode = text("solution_code")
    val solutionVisibility = enumeration<AssignmentCode.SolutionVisibility>("solution_visibility")
    val unitTest = text("unit_test")
    val primary = bool("primary")
    val injectable = bool("injectable")

    init {
        uniqueIndex(assignment, language)
    }
}

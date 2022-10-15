package org.empowrco.coppin.db

object StarterCodes : BaseTable() {
    val assignment = reference("assignment", Assignments.id)
    val language = reference("language", Languages.id)
    val code = text("code")
    val primary = bool("primary")

    init {
        uniqueIndex(assignment, language, primary)
    }
}

package org.empowrco.coppin.db

object Assignments : BaseTable() {
    val referenceId = text("reference_id").uniqueIndex()
    val expectedOutput = text("expected_output")
    val instructions = text("instructions")
    val totalAttempts = integer("total_attempts")
    val successMessage = text("success_message")
    val failureMessage = text("failure_message")
    val title = text("title")
}

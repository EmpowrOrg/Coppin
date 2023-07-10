package org.empowrco.coppin.db

object Assignments : BaseTable() {
    val referenceId = text("reference_id").uniqueIndex()
    val instructions = text("instructions")
    val totalAttempts = integer("total_attempts")
    val successMessage = text("success_message")
    val failureMessage = text("failure_message")
    val title = text("title")
    val courseId = reference("course_id", Courses.id)
    val blockId = text("block_id").nullable()
}

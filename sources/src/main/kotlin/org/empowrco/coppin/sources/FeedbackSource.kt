package org.empowrco.coppin.sources

import org.empowrco.coppin.db.Feedbacks
import org.empowrco.coppin.models.Feedback
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface FeedbackSource {
    suspend fun create(feedback: List<Feedback>)
    suspend fun deleteByAssignment(assignmentId: UUID): Boolean
    suspend fun getFeedback(assignmentId: UUID): List<Feedback>
    suspend fun updateFeedback(feedback: Feedback): Boolean
    suspend fun deleteFeedback(id: UUID): Boolean
}

internal class RealFeedbackSource: FeedbackSource {
    override suspend fun create(feedback: List<Feedback>) = dbQuery {
        Feedbacks.batchInsert(feedback) {
            feedbackBuilder(it, false)
        }
        Unit
    }

    override suspend fun deleteByAssignment(assignmentId: UUID): Boolean = dbQuery {
        Feedbacks.deleteWhere { Feedbacks.assignmentId eq  assignmentId } > 0
    }

    override suspend fun getFeedback(assignmentId: UUID): List<Feedback> = dbQuery {
        Feedbacks.select { Feedbacks.assignmentId eq assignmentId }.map { it.toFeedback() }
    }

    override suspend fun updateFeedback(feedback: Feedback): Boolean = dbQuery {
        Feedbacks.update({Feedbacks.id eq feedback.id}) {
            it.feedbackBuilder(feedback, true)
        } > 0
    }

    override suspend fun deleteFeedback(id: UUID): Boolean = dbQuery {
        Feedbacks.deleteWhere { Feedbacks.id eq id } > 0
    }
}

private fun UpdateBuilder<*>.feedbackBuilder(
    it: Feedback,
    isUpdate: Boolean
) {
    this[Feedbacks.id] = it.id
    this[Feedbacks.feedback] = it.feedback
    this[Feedbacks.regexMatcher] = it.regexMatcher
    this[Feedbacks.attempt] = it.attempt
    this[Feedbacks.assignmentId] = it.assignmentId
    if (!isUpdate) {
        this[Feedbacks.createdAt] = it.createdAt
    }
    this[Feedbacks.lastModifiedAt] = it.lastModifiedAt
}

private fun ResultRow.toFeedback(): Feedback {
    return Feedback(
        id = this[Feedbacks.id].value,
        attempt = this[Feedbacks.attempt],
        regexMatcher = this[Feedbacks.regexMatcher],
        assignmentId = this[Feedbacks.assignmentId].value,
        feedback = this[Feedbacks.feedback],
        createdAt = this[Feedbacks.createdAt],
        lastModifiedAt = this[Feedbacks.lastModifiedAt]
    )
}

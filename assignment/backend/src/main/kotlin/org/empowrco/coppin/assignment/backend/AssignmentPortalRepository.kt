package org.empowrco.coppin.assignment.backend

import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Feedback
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.sources.AssignmentCodesSource
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.FeedbackSource
import org.empowrco.coppin.sources.LanguagesSource
import java.util.UUID


interface AssignmentPortalRepository {
    suspend fun getAssignments(): List<Assignment>
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun createAssignment(assignment: Assignment)
    suspend fun getLanguage(mime: String): Language?
    suspend fun deleteAssignment(id: UUID): Boolean
    suspend fun getCode(id: UUID): AssignmentCode?
    suspend fun updateCode(code: AssignmentCode): Boolean
    suspend fun saveCode(code: AssignmentCode)
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getLanguages(): List<Language>
    suspend fun getFeedback(id: UUID): Feedback?
    suspend fun saveFeedback(feedback: Feedback)
    suspend fun updateFeedback(feedback: Feedback): Boolean
    suspend fun deleteFeedback(id: UUID)
    suspend fun deleteCode(id: UUID)
    suspend fun deprimaryAssignmentCodes(assignmentId: UUID)
}

internal class RealAssignmentPortalRepository(
    private val assignmentSource: AssignmentSource,
    private val languagesSource: LanguagesSource,
    private val codesSource: AssignmentCodesSource,
    private val feedbackSource: FeedbackSource,
) : AssignmentPortalRepository {

    override suspend fun getAssignments(): List<Assignment> {
        return assignmentSource.getAssignments()
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        return assignmentSource.updateAssignment(assignment)
    }

    override suspend fun getLanguages(): List<Language> {
        return languagesSource.getLanguages()
    }

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignmentSource.getAssignment(id)
    }

    override suspend fun getCode(id: UUID): AssignmentCode? {
        return codesSource.get(id)
    }

    override suspend fun getLanguage(mime: String): Language? {
        return languagesSource.getLanguageByMime(mime)
    }

    override suspend fun getFeedback(id: UUID): Feedback? {
        return feedbackSource.getFeedback(id)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        return assignmentSource.createAssignment(assignment)
    }

    override suspend fun deleteAssignment(id: UUID): Boolean {
        return assignmentSource.deleteAssignment(id)
    }

    override suspend fun updateCode(code: AssignmentCode): Boolean {
        return codesSource.update(code)
    }

    override suspend fun saveCode(code: AssignmentCode) {
        codesSource.create(listOf(code))
    }

    override suspend fun saveFeedback(feedback: Feedback) {
        feedbackSource.create(listOf(feedback))
    }

    override suspend fun updateFeedback(feedback: Feedback): Boolean {
        return feedbackSource.updateFeedback(feedback)
    }

    override suspend fun deleteFeedback(id: UUID) {
        feedbackSource.deleteFeedback(id)
    }

    override suspend fun deprimaryAssignmentCodes(assignmentId: UUID) {
        codesSource.deprimaryAssignmentCodes(assignmentId)
    }

    override suspend fun deleteCode(id: UUID) {
        codesSource.delete(id)
    }
}

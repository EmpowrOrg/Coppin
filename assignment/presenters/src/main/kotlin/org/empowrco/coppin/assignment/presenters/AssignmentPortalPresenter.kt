package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.LocalDateTime
import org.apache.commons.text.StringEscapeUtils
import org.empowrco.coppin.assignment.backend.AssignmentPortalRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Feedback
import org.empowrco.coppin.models.portal.AssignmentCodeItem
import org.empowrco.coppin.models.portal.AssignmentItem
import org.empowrco.coppin.models.portal.AssignmentListItem
import org.empowrco.coppin.models.portal.CodeListItem
import org.empowrco.coppin.models.portal.FeedbackItem
import org.empowrco.coppin.models.portal.FeedbackListItem
import org.empowrco.coppin.utils.InvalidUuidException
import org.empowrco.coppin.utils.UnknownException
import org.empowrco.coppin.utils.ellipsize
import org.empowrco.coppin.utils.now
import java.util.UUID

interface AssignmentPortalPresenter {
    suspend fun getAssignments(): List<AssignmentListItem>
    suspend fun getAssignment(id: String): GetAssignmentPortalResponse
    suspend fun deleteAssignment(id: String)
    suspend fun updateAssignment(request: UpdateAssignmentPortalRequest)
    suspend fun getCode(id: String?, assignmentId: String): AssignmentCodeItem
    suspend fun getFeedback(id: String?, assignmentId: String): FeedbackItem
    suspend fun saveCode(request: UpdateCodePortalRequest)
    suspend fun createAssignment(request: CreateAssignmentPortalRequest): UUID
    suspend fun saveFeedback(request: SaveFeedbackRequest)
    suspend fun deleteFeedback(id: String)
    suspend fun deleteCode(id: String)
}

internal class RealAssignmentPortalPresenter(private val repo: AssignmentPortalRepository) : AssignmentPortalPresenter {
    override suspend fun getAssignments(): List<AssignmentListItem> {
        return repo.getAssignments().map {
            val createdDate = it.createdAt
            AssignmentListItem(
                referenceId = it.referenceId,
                id = it.id.toString(),
                createdAt = "${createdDate.monthNumber}/${createdDate.dayOfMonth}/${createdDate.year}",
                title = it.title.ellipsize(),
            )
        }
    }

    override suspend fun updateAssignment(request: UpdateAssignmentPortalRequest) {
        val uuid = UUID.fromString(request.id) ?: throw InvalidUuidException("id")
        val assignment = repo.getAssignment(uuid) ?: throw NotFoundException()
        val currentTime = LocalDateTime.now()
        val gradingType = if (request.gradingType == "unit-tests") {
            Assignment.GradingType.UnitTests
        } else {
            Assignment.GradingType.Output
        }
        val updatedAssignment = assignment.copy(
            failureMessage = request.failureMessage,
            successMessage = request.successMessage,
            gradingType = gradingType,
            instructions = request.instructions,
            totalAttempts = request.totalAttempts,
            title = request.title,
            lastModifiedAt = currentTime,
        )
        val result = repo.updateAssignment(updatedAssignment)
        if (!result) {
            throw UnknownException
        }
    }

    override suspend fun createAssignment(request: CreateAssignmentPortalRequest): UUID {
        val currentTime = LocalDateTime.now()
        val id = UUID.randomUUID()
        val gradingType = if (request.gradingType == "unit-tests") {
            Assignment.GradingType.UnitTests
        } else {
            Assignment.GradingType.Output
        }

        val assignment = Assignment(
            id = id,
            failureMessage = request.failureMessage,
            successMessage = request.successMessage,
            instructions = request.instructions,
            gradingType = gradingType,
            totalAttempts = request.totalAttempts,
            referenceId = request.referenceId,
            feedback = emptyList(),
            assignmentCodes = emptyList(),
            title = request.title,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
            expectedOutput = request.expectedOutput,
        )
        repo.createAssignment(assignment)
        return id
    }

    override suspend fun getAssignment(id: String): GetAssignmentPortalResponse {
        val assignmentId = UUID.fromString(id) ?: throw InvalidUuidException("id")
        val assignment = repo.getAssignment(assignmentId) ?: throw NotFoundException()
        val assignmentItem = AssignmentItem(
            title = assignment.title,
            gradingType = assignment.gradingType,
            successMessage = StringEscapeUtils.escapeJava(assignment.successMessage),
            referenceId = "Reference Id: ${assignment.referenceId}",
            failureMessage = StringEscapeUtils.escapeJava(assignment.failureMessage),
            attempts = assignment.totalAttempts,
            id = assignment.id.toString(),
            instructions = StringEscapeUtils.escapeJava(assignment.instructions),
            expectedOutput = assignment.expectedOutput
        )
        val codes = assignment.assignmentCodes.map {
            CodeListItem(
                id = it.id.toString(),
                language = it.language.name,
                primary = if (it.primary) "True" else "",
                hasSolution = if (it.solutionCode.isNotBlank()) "True" else "",
                hasStarter = if (it.starterCode.isNotBlank()) "True" else "",
                hasUnitTests = if (it.unitTest.isNullOrBlank()) "" else "True",
                assignmentId = assignment.id.toString(),
            )
        }
        val feedback = assignment.feedback.map {
            FeedbackListItem(
                id = it.id.toString(),
                feedback = it.feedback.ellipsize(),
                attempt = if (it.attempt == 0) "Any" else it.attempt.toString(),
                regex = it.regexMatcher,
                assignmentId = assignment.id.toString()
            )
        }
        return GetAssignmentPortalResponse(
            assignment = assignmentItem,
            codes = codes,
            feedback = feedback,
        )
    }

    override suspend fun deleteAssignment(id: String) {
        val assignmentId = UUID.fromString(id) ?: throw InvalidUuidException("id")
        repo.deleteAssignment(assignmentId)
    }

    override suspend fun getCode(id: String?, assignmentId: String): AssignmentCodeItem {
        val languages = repo.getLanguages().map {
            AssignmentCodeItem.Language(
                name = it.name,
                id = it.id.toString(),
                url = it.url,
                mime = it.mime,
            )
        }
        val assignmentId = UUID.fromString(assignmentId)
        val assignment = repo.getAssignment(assignmentId) ?: throw NotFoundException("")
        if (id == null) {
            return AssignmentCodeItem(
                id = "",
                starterCode = "",
                solutionCode = "",
                languages = languages,
                assignmentId = assignment.id.toString(),
                unitTest = if (assignment.gradingType == Assignment.GradingType.UnitTests) "" else null,
                primary = false,
                language = languages.first()
            )
        }
        val assignmentCodeId = UUID.fromString(id) ?: throw InvalidUuidException("id")
        val code = repo.getCode(assignmentCodeId) ?: throw NotFoundException()
        return AssignmentCodeItem(
            id = code.id.toString(),
            primary = code.primary,
            starterCode = StringEscapeUtils.escapeJava(code.starterCode),
            solutionCode = StringEscapeUtils.escapeJava(code.solutionCode),
            assignmentId = code.assignmentId.toString(),
            unitTest = StringEscapeUtils.escapeJava(code.unitTest),
            language = AssignmentCodeItem.Language(
                name = code.language.name,
                id = code.language.id.toString(),
                mime = code.language.mime,
                url = code.language.url,
            ),
            languages = languages,
        )
    }

    override suspend fun getFeedback(id: String?, assignmentId: String): FeedbackItem {
        id ?: return FeedbackItem(id = "", attempt = 0, feedback = "", assignmentId = assignmentId, regex = "")
        val feedback = repo.getFeedback(UUID.fromString(id)) ?: throw NotFoundException()
        return FeedbackItem(
            id = feedback.id.toString(),
            feedback = feedback.feedback,
            regex = feedback.regexMatcher,
            assignmentId = assignmentId,
            attempt = feedback.attempt,
        )
    }

    override suspend fun saveCode(request: UpdateCodePortalRequest) {
        val currentTime = LocalDateTime.now()
        val language = repo.getLanguage(request.languageMime) ?: throw NotFoundException("language")
        var primary = request.primary == "on"
        // new assignment code
        val assignmentId = UUID.fromString(request.assignmentId)
        if (request.id == null) {
            val codes = repo.getAssignmentCodes(assignmentId)
            if (codes.isEmpty()) {
                primary = true
            }
            val code = AssignmentCode(
                id = UUID.randomUUID(),
                language = language,
                primary = primary,
                assignmentId = UUID.fromString(request.assignmentId),
                starterCode = request.starterCode ?: "",
                solutionCode = request.solutionCode ?: "",
                unitTest = request.unitTest,
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
            repo.saveCode(code)
        } else { // updating existing assignment code
            if (primary) {
                repo.deprimaryAssignmentCodes(assignmentId)
            }
            val assignmentCodeId = UUID.fromString(request.id) ?: throw InvalidUuidException("id")
            val code = repo.getCode(assignmentCodeId) ?: throw NotFoundException()
            val updatedCode = code.copy(
                starterCode = request.starterCode ?: "",
                solutionCode = request.solutionCode ?: "",
                language = language,
                primary = primary,
                unitTest = request.unitTest,
                lastModifiedAt = currentTime,
            )
            val result = repo.updateCode(updatedCode)
            if (!result) {
                throw UnknownException
            }
        }
    }

    override suspend fun saveFeedback(request: SaveFeedbackRequest) {
        val assignmentId = UUID.fromString(request.assignmentId)
        val currentTime = LocalDateTime.now()
        request.id ?: run {
            val feedback = Feedback(
                id = UUID.randomUUID(),
                feedback = request.feedback,
                regexMatcher = request.regex,
                attempt = request.attempt,
                assignmentId = assignmentId,
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
            repo.saveFeedback(feedback)
            return
        }
        val feedback = repo.getFeedback(UUID.fromString(request.id)) ?: throw NotFoundException()
        val updateFeedback = feedback.copy(
            feedback = request.feedback,
            regexMatcher = request.regex,
            attempt = request.attempt,
            lastModifiedAt = currentTime,
        )
        val result = repo.updateFeedback(updateFeedback)
        if (!result) {
            throw UnknownException
        }
    }

    override suspend fun deleteFeedback(id: String) {
        repo.deleteFeedback(UUID.fromString(id))
    }

    override suspend fun deleteCode(id: String) {
        val uuid = UUID.fromString(id)
        val code = repo.getCode(uuid) ?: throw NotFoundException()
        repo.deleteCode(uuid)
        val codes = repo.getAssignmentCodes(code.assignmentId)
        if (codes.size == 1) {
            repo.updateCode(codes.first().copy(primary = true))
        }
    }
}

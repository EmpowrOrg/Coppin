package org.empowrco.coppin.assignment.presenters

import kotlinx.datetime.LocalDateTime
import org.apache.commons.text.StringEscapeUtils
import org.empowrco.coppin.assignment.backend.AssignmentPortalRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Feedback
import org.empowrco.coppin.models.portal.AssignmentItem
import org.empowrco.coppin.models.portal.CodeListItem
import org.empowrco.coppin.models.portal.FeedbackListItem
import org.empowrco.coppin.utils.ellipsize
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

interface AssignmentPortalPresenter {
    suspend fun getAssignments(): Result<GetAssignmentsResponse>
    suspend fun getAssignment(id: String): Result<GetAssignmentPortalResponse>
    suspend fun updateAssignment(request: UpdateAssignmentPortalRequest): Result<UpdateAssignmentResponse>
    suspend fun getCode(id: String?, assignmentId: String): Result<GetCodeResponse>
    suspend fun getFeedback(id: String?, assignmentId: String): Result<FeedbackResponse>
    suspend fun saveCode(request: UpdateCodePortalRequest): Result<SaveAssignmentResponse>
    suspend fun createAssignment(request: CreateAssignmentPortalRequest): Result<CreateAssignmentResponse>
    suspend fun saveFeedback(request: SaveFeedbackRequest): Result<SaveFeedbackResponse>
    suspend fun deleteFeedback(id: String): Result<DeleteFeedbackResponse>
    suspend fun deleteCode(id: String): Result<DeleteCodeResponse>
}

internal class RealAssignmentPortalPresenter(private val repo: AssignmentPortalRepository) : AssignmentPortalPresenter {
    override suspend fun getAssignments(): Result<GetAssignmentsResponse> {
        return GetAssignmentsResponse(repo.getAssignments().map {
            val createdDate = it.createdAt
            GetAssignmentsResponse.AssignmentListItem(
                referenceId = it.referenceId,
                id = it.id.toString(),
                createdAt = "${createdDate.monthNumber}/${createdDate.dayOfMonth}/${createdDate.year}",
                title = it.title.ellipsize(),
            )
        }).toResult()
    }

    override suspend fun updateAssignment(request: UpdateAssignmentPortalRequest): Result<UpdateAssignmentResponse> {
        val uuid = request.id.toUuid() ?: return failure("Invalid id")
        val assignment = repo.getAssignment(uuid) ?: return failure("Assignment not found")
        val currentTime = LocalDateTime.now()
        val updatedAssignment = assignment.copy(
            failureMessage = request.failureMessage,
            successMessage = request.successMessage,
            instructions = request.instructions,
            totalAttempts = request.totalAttempts,
            title = request.title,
            lastModifiedAt = currentTime,
        )
        val result = repo.updateAssignment(updatedAssignment)
        if (!result) {
            return failure("Unknown error")
        }
        return UpdateAssignmentResponse.toResult()
    }

    override suspend fun createAssignment(request: CreateAssignmentPortalRequest): Result<CreateAssignmentResponse> {
        val currentTime = LocalDateTime.now()
        val id = UUID.randomUUID()

        val assignment = Assignment(
            id = id,
            failureMessage = request.failureMessage,
            successMessage = request.successMessage,
            instructions = request.instructions,
            totalAttempts = request.totalAttempts,
            referenceId = request.referenceId,
            feedback = emptyList(),
            assignmentCodes = emptyList(),
            title = request.title,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        repo.createAssignment(assignment)
        return CreateAssignmentResponse(id.toString()).toResult()
    }

    override suspend fun getAssignment(id: String): Result<GetAssignmentPortalResponse> {
        val assignmentId = id.toUuid() ?: return failure("invalid id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("Assignment not found")
        val assignmentItem = AssignmentItem(
            title = assignment.title,
            successMessage = StringEscapeUtils.escapeJava(assignment.successMessage),
            referenceId = "Reference Id: ${assignment.referenceId}",
            failureMessage = StringEscapeUtils.escapeJava(assignment.failureMessage),
            attempts = assignment.totalAttempts,
            id = assignment.id.toString(),
            instructions = StringEscapeUtils.escapeJava(assignment.instructions),
        )
        val codes = assignment.assignmentCodes.map {
            CodeListItem(
                id = it.id.toString(),
                language = it.language.name,
                primary = if (it.primary) "True" else "",
                hasSolution = if (it.solutionCode.isNotBlank()) "True" else "",
                hasStarter = if (it.starterCode.isNotBlank()) "True" else "",
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
        ).toResult()
    }

    override suspend fun getCode(id: String?, assignmentIdString: String): Result<GetCodeResponse> {
        val languages = repo.getLanguages().map {
            GetCodeResponse.AssignmentCodeItem.Language(
                name = it.name,
                id = it.id.toString(),
                url = it.url,
                mime = it.mime,
            )
        }
        val assignmentId = assignmentIdString.toUuid() ?: return failure("Invalid assignment id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("No assignment found")
        if (id == null) {
            return GetCodeResponse(
                code = GetCodeResponse.AssignmentCodeItem(
                    id = "",
                    starterCode = "",
                    solutionCode = "",
                    assignmentId = assignment.id.toString(),
                    unitTest = "",
                    language = languages.first(),
                    primary = false,
                    languages = languages,
                )
            ).toResult()
        }
        val assignmentCodeId = id.toUuid() ?: return failure("Invalid id")
        val code = repo.getCode(assignmentCodeId) ?: return failure("Code not found")
        return GetCodeResponse(
            code = GetCodeResponse.AssignmentCodeItem(
                id = code.id.toString(),
                primary = code.primary,
                starterCode = StringEscapeUtils.escapeJava(code.starterCode),
                solutionCode = StringEscapeUtils.escapeJava(code.solutionCode),
                assignmentId = code.assignmentId.toString(),
                unitTest = StringEscapeUtils.escapeJava(code.unitTest),
                language = GetCodeResponse.AssignmentCodeItem.Language(
                    name = code.language.name,
                    id = code.language.id.toString(),
                    mime = code.language.mime,
                    url = code.language.url,
                ),
                languages = languages,
            )
        ).toResult()
    }

    override suspend fun getFeedback(id: String?, assignmentId: String): Result<FeedbackResponse> {
        val uuid = id?.toUuid() ?: return FeedbackResponse(
            id = "",
            attempt = 0,
            feedback = "",
            assignmentId = assignmentId,
            regex = ""
        ).toResult()
        val feedback = repo.getFeedback(uuid) ?: return failure("Feedback not found")
        return FeedbackResponse(
            id = feedback.id.toString(),
            feedback = feedback.feedback,
            regex = feedback.regexMatcher,
            assignmentId = assignmentId,
            attempt = feedback.attempt,
        ).toResult()
    }

    override suspend fun saveCode(request: UpdateCodePortalRequest): Result<SaveAssignmentResponse> {
        val currentTime = LocalDateTime.now()
        val language = repo.getLanguage(request.languageMime) ?: return failure("Language not found")
        var primary = request.primary == "on"
        // new assignment code
        val assignmentId = request.assignmentId.toUuid() ?: return failure("No assignment found for this id")
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
            val assignmentCodeId = request.id.toUuid() ?: return failure("Invalid id")
            val code = repo.getCode(assignmentCodeId) ?: return failure("Code not found")
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
                return failure("Unknown error")
            }
        }
        return SaveAssignmentResponse.toResult()
    }

    override suspend fun saveFeedback(request: SaveFeedbackRequest): Result<SaveFeedbackResponse> {
        val assignmentId = request.assignmentId.toUuid() ?: return failure("No assignment found")
        val currentTime = LocalDateTime.now()
        val feedbackId = request.id?.toUuid()
        feedbackId ?: run {
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
            return SaveFeedbackResponse.toResult()
        }
        val feedback = repo.getFeedback(feedbackId) ?: return failure("Feedback not found")
        val updateFeedback = feedback.copy(
            feedback = request.feedback,
            regexMatcher = request.regex,
            attempt = request.attempt,
            lastModifiedAt = currentTime,
        )
        val result = repo.updateFeedback(updateFeedback)
        if (!result) {
            return failure("Unknown failure")
        }
        return SaveFeedbackResponse.toResult()
    }

    override suspend fun deleteFeedback(id: String): Result<DeleteFeedbackResponse> {
        val feedbackId = id.toUuid() ?: return failure("Invalid feedback id")
        repo.deleteFeedback(feedbackId)
        return DeleteFeedbackResponse.toResult()
    }

    override suspend fun deleteCode(id: String): Result<DeleteCodeResponse> {
        val uuid = id.toUuid() ?: return failure("Invalid code id")
        val code = repo.getCode(uuid) ?: return failure("Code not found")
        repo.deleteCode(uuid)
        val codes = repo.getAssignmentCodes(code.assignmentId)
        if (codes.size == 1) {
            repo.updateCode(codes.first().copy(primary = true))
        }
        return DeleteCodeResponse.toResult()
    }
}

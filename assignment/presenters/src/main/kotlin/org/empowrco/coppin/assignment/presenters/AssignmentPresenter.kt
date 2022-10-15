package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.assignment.backend.AssignmentRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Feedback
import org.empowrco.coppin.models.StarterCode
import org.empowrco.coppin.utils.InvalidUuidException
import org.empowrco.coppin.utils.diff.DiffUtil
import org.empowrco.coppin.utils.now
import java.util.UUID

interface AssignmentPresenter {
    suspend fun submit(request: SubmitRequest): SubmitResponse
    suspend fun create(request: CreateAssignmentRequest): CreateAssignmentResponse
    suspend fun get(request: GetAssignmentRequest): GetAssignmentResponse
}

internal class RealAssignmentPresenter(
    private val repo: AssignmentRepository,
    private val diffUtil: DiffUtil,
) : AssignmentPresenter {

    override suspend fun submit(request: SubmitRequest): SubmitResponse {
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException("Assignment not found")
        val isFinalAttempt = request.attempt >= assignment.totalAttempts

        if (assignment.totalAttempts > 0 && request.attempt > assignment.totalAttempts) {
            return SubmitResponse(
                output = assignment.failureMessage,
                expectedOutput = assignment.expectedOutput,
                success = false,
                finalAttempt = true,
                feedback = "",
                diff = null,
            )
        }

        return if (!request.executeSuccess) {
            val error = request.output
            if (assignment.feedback.isEmpty()) {
                return SubmitResponse(
                    output = error,
                    expectedOutput = assignment.expectedOutput,
                    success = false,
                    finalAttempt = isFinalAttempt,
                    feedback = "",
                    diff = null,
                )
            }
            val feedback = getFeedback(assignment, request, error)
            SubmitResponse(
                output = error,
                expectedOutput = assignment.expectedOutput,
                success = false,
                finalAttempt = isFinalAttempt,
                feedback = feedback,
                diff = null,
            )
        } else {
            if (assignment.expectedOutput == request.output) {
                SubmitResponse(
                    output = request.output,
                    expectedOutput = assignment.expectedOutput,
                    success = true,
                    finalAttempt = isFinalAttempt,
                    feedback = assignment.successMessage,
                    diff = null,
                )
            } else {
                val feedback = getFeedback(assignment, request, request.output)
                SubmitResponse(
                    output = request.output,
                    expectedOutput = assignment.expectedOutput,
                    success = false,
                    finalAttempt = isFinalAttempt,
                    feedback = feedback,
                    diff = diffUtil.generateDiffHtml(request.output, assignment.expectedOutput),
                )
            }
        }
    }

    private fun getFeedback(
        assignment: Assignment,
        request: SubmitRequest,
        output: String
    ): String {
        val validAttemptFeedback = assignment.feedback.filter {
            it.attempt <= request.attempt
        }.sortedByDescending { it.attempt }
        val feedback = validAttemptFeedback.firstOrNull {
            if (it.regexMatcher.isBlank()) {
                return@firstOrNull false
            }
            it.regex.matches(output)
        } ?: validAttemptFeedback.firstOrNull {
            it.regexMatcher.isBlank()
        }
        return feedback?.feedback ?: ""
    }

    override suspend fun create(request: CreateAssignmentRequest): CreateAssignmentResponse {
        val currentTime = LocalDateTime.now()
        val assignmentId = UUID.randomUUID()
        val primariesSelected = request.starterCodes.filter { it.primary }.size
        if (primariesSelected == 0) {
            throw BadRequestException("Must select a primary starter code")
        } else if (primariesSelected > 1) {
            throw BadRequestException("Only one primary starter code should be selected")
        }
        val assignment = Assignment(
            id = assignmentId,
            referenceId = request.referenceId,
            failureMessage = request.failureMessage,
            successMessage = request.successMessage,
            instructions = request.instructions,
            expectedOutput = request.expectedOutput,
            solution = request.solution,
            totalAttempts = request.totalAttempts,
            title = request.title,
            feedback = request.feedback.map {
                Feedback(
                    id = UUID.randomUUID(),
                    feedback = it.feedback,
                    regexMatcher = it.regex,
                    attempt = it.attempt,
                    createdAt = currentTime,
                    lastModifiedAt = currentTime,
                    assignmentId = assignmentId
                )
            },
            starterCodes = request.starterCodes.map {
                val languageUuid = UUID.fromString(it.languageId) ?: throw InvalidUuidException("languageId")
                val language =
                    repo.getLanguage(languageUuid) ?: throw NotFoundException("language not found for $languageUuid")
                StarterCode(
                    id = UUID.randomUUID(),
                    assignmentId = assignmentId,
                    code = it.code,
                    language = language,
                    primary = it.primary,
                    createdAt = currentTime,
                    lastModifiedAt = currentTime,
                )
            },
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        repo.createAssignment(assignment)
        return CreateAssignmentResponse
    }

    override suspend fun get(request: GetAssignmentRequest): GetAssignmentResponse {
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException()
        val shouldFilter = request.supportedLanguageMimes.isNotEmpty()
        val starterCodes = assignment.starterCodes.mapNotNull {
            if (shouldFilter && !request.supportedLanguageMimes.contains(it.language.mime)) {
                return@mapNotNull null
            }
            GetAssignmentResponse.StarterCode(
                displayName = it.language.name,
                mime = it.language.mime,
                starterCode = it.code,
                primary = it.primary,
            )
        }
        return GetAssignmentResponse(
            instructions = assignment.instructions,
            title = assignment.title,
            solution = assignment.solution,
            starterCodes = starterCodes,
        )
    }
}

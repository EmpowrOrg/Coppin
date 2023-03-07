package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.NotFoundException
import org.empowrco.coppin.assignment.backend.AssignmentRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.utils.AssignmentLanguageSupportException
import org.empowrco.coppin.utils.LanguageSupportException
import org.empowrco.coppin.utils.diff.DiffUtil

interface AssignmentPresenter {
    suspend fun submit(request: SubmitRequest): SubmitResponse
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
                success = false,
                finalAttempt = true,
                feedback = "",
                diff = null,
            )
        }

        val assignmentCode =
            assignment.assignmentCodes.find {
                it.language.mime.equals(
                    request.language,
                    ignoreCase = true
                ) || it.language.name.equals(request.language, ignoreCase = true)
            } ?: run {
                throw AssignmentLanguageSupportException(request.language)
            }
        if (!assignmentCode.language.supportsUnitTests) {
            throw LanguageSupportException(assignmentCode.language.name)
        }
        if (assignmentCode.unitTest == null) {
            throw RuntimeException("No unit test created for this assignment")
        }
        val codeResponse = repo.testCode(request.language, request.code, assignmentCode.unitTest!!)

        return if (!codeResponse.success) {
            val error = codeResponse.output
            if (assignment.feedback.isEmpty()) {
                return SubmitResponse(
                    output = error,
                    success = false,
                    finalAttempt = isFinalAttempt,
                    feedback = "",
                    diff = null,
                )
            }
            val feedback = getFeedback(assignment, request, error)
            SubmitResponse(
                output = error,
                success = false,
                finalAttempt = isFinalAttempt,
                feedback = feedback,
                diff = null,
            )
        } else {
            val matches = "(?<=XCTAssertEqual failed:).*\\n".toRegex().findAll(codeResponse.output).toList()
            return if (matches.isNotEmpty()) {
                SubmitResponse(
                    output = matches.first().value,
                    success = false,
                    finalAttempt = isFinalAttempt,
                    feedback = matches.first().value,
                    diff = null,
                )
            } else {
                SubmitResponse(
                    output = assignment.successMessage,
                    success = true,
                    finalAttempt = isFinalAttempt,
                    feedback = assignment.successMessage,
                    diff = null,
                )
            }


        }
    }


    private fun getFeedback(
        assignment: Assignment,
        request: SubmitRequest,
        output: String,
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

    override suspend fun get(request: GetAssignmentRequest): GetAssignmentResponse {
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException()
        val shouldFilter = request.supportedLanguageMimes.isNotEmpty()
        val assignmentCodes = assignment.assignmentCodes.mapNotNull {
            if (shouldFilter && !request.supportedLanguageMimes.contains(it.language.mime)) {
                return@mapNotNull null
            }
            GetAssignmentResponse.AssignmentCode(
                displayName = it.language.name,
                mime = it.language.mime,
                starterCode = it.starterCode,
                primary = it.primary,
                solutionCode = it.solutionCode
            )
        }.toMutableList()
        if (assignmentCodes.isEmpty()) {
            repo.getLanguages().forEachIndexed { index, language ->
                if (shouldFilter && !request.supportedLanguageMimes.contains(language.mime)) {
                    return@forEachIndexed
                }
                assignmentCodes.add(
                    GetAssignmentResponse.AssignmentCode(
                        displayName = language.name,
                        mime = language.mime,
                        starterCode = "",
                        primary = index == 0,
                        solutionCode = "",
                    )
                )
            }
        }
        return GetAssignmentResponse(
            instructions = assignment.instructions,
            title = assignment.title,
            assignmentCodes = assignmentCodes,
        )
    }
}

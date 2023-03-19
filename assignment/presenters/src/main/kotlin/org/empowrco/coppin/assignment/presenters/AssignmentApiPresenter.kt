package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import org.empowrco.coppin.assignment.backend.AssignmentApiRepository
import org.empowrco.coppin.assignment.presenters.RequestApi.DeleteAssignmentRequest
import org.empowrco.coppin.assignment.presenters.RequestApi.GetAssignmentRequest
import org.empowrco.coppin.assignment.presenters.RequestApi.RunRequest
import org.empowrco.coppin.assignment.presenters.RequestApi.SubmitRequest
import org.empowrco.coppin.assignment.presenters.ResponseApi.DeleteAssignmentResponse
import org.empowrco.coppin.assignment.presenters.ResponseApi.GetAssignmentResponse
import org.empowrco.coppin.assignment.presenters.ResponseApi.RunResponse
import org.empowrco.coppin.assignment.presenters.ResponseApi.SubmitResponse
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.utils.AssignmentLanguageSupportException
import org.empowrco.coppin.utils.InvalidUuidException
import org.empowrco.coppin.utils.UnknownException
import org.empowrco.coppin.utils.toUuid

interface AssignmentApiPresenter {
    suspend fun run(request: RunRequest): RunResponse
    suspend fun submit(request: SubmitRequest): SubmitResponse
    suspend fun get(request: GetAssignmentRequest): GetAssignmentResponse
    suspend fun deleteAssignment(request: DeleteAssignmentRequest): DeleteAssignmentResponse
}

internal class RealAssignmentApiPresenter(
    private val repo: AssignmentApiRepository,
) : AssignmentApiPresenter {

    override suspend fun run(request: RunRequest): RunResponse {
        repo.getAssignment(request.referenceId) ?: throw NotFoundException("Assignment not found")
        val language = getLanguage(request.language) ?: throw NotFoundException("Language not found")
        if (request.code.isBlank()) {
            throw BadRequestException("The code is not blank")
        }

        val response = repo.runCode(language.mime, request.code)
        return RunResponse(response.output, response.success ?: true)
    }

    private suspend fun getLanguage(language: String): Language? {
        return repo.getLanguages().firstOrNull {
            (it.name.equals(language, ignoreCase = true) ||
                    it.mime.equals(language, ignoreCase = true))
        }
    }

    private fun getAssignmentCode(
        assignment: Assignment,
        languageName: String,
    ): AssignmentCode {
        val assignmentCode =
            assignment.assignmentCodes.find {
                it.language.name.equals(languageName, ignoreCase = true)
            } ?: run {
                throw AssignmentLanguageSupportException(languageName)
            }
        return assignmentCode
    }

    override suspend fun submit(request: SubmitRequest): SubmitResponse {
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException("Assignment not found")
        val isFinalAttempt = request.attempt >= assignment.totalAttempts

        if (assignment.totalAttempts > 0 && request.attempt > assignment.totalAttempts) {
            return SubmitResponse(
                output = assignment.failureMessage,
                success = false,
                finalAttempt = true,
                diff = null,
            )
        }
        val language = getLanguage(request.language) ?: throw NotFoundException("Language not found")
        val assignmentCode = getAssignmentCode(assignment, language.name)
        if (assignmentCode.unitTest.isBlank()) {
            throw RuntimeException("No unit test created for this assignment")
        }
        val code = if (assignmentCode.injectable) {
            assignmentCode.starterCode.replace("{{code}}", request.code)
        } else {
            request.code
        }
        val codeResponse = repo.testCode(language.mime, code, assignmentCode.unitTest)
        val languageRegex = assignmentCode.language.unitTestRegex.toRegex()
        val matches = languageRegex.findAll(codeResponse.output).toList()
        return if (matches.isNotEmpty()) {
            SubmitResponse(
                output = matches.first().value,
                success = false,
                finalAttempt = isFinalAttempt,
                diff = null,
            )
        } else if (codeResponse.success == false) {
            SubmitResponse(
                output = codeResponse.output,
                success = false,
                finalAttempt = isFinalAttempt,
                diff = null,
            )
        } else {
            SubmitResponse(
                output = assignment.successMessage,
                success = true,
                finalAttempt = isFinalAttempt,
                diff = null,
            )
        }
    }

    override suspend fun get(request: GetAssignmentRequest): GetAssignmentResponse {
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException()
        val shouldFilter = request.supportedLanguageMimes.isNotEmpty()
        val assignmentCodes = assignment.assignmentCodes.mapNotNull {
            if (shouldFilter && !request.supportedLanguageMimes.contains(it.language.mime)) {
                return@mapNotNull null
            }
            val starterCode = if (it.injectable) {
                ""
            } else {
                it.starterCode
            }
            GetAssignmentResponse.AssignmentCode(
                displayName = it.language.name,
                mime = it.language.mime,
                starterCode = starterCode,
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

    override suspend fun deleteAssignment(request: DeleteAssignmentRequest): DeleteAssignmentResponse {
        val assignmentId = request.id.toUuid() ?: throw InvalidUuidException("id")
        val result = repo.deleteAssignment(assignmentId)
        if (!result) {
            throw UnknownException
        }
        return DeleteAssignmentResponse(request.id)
    }
}

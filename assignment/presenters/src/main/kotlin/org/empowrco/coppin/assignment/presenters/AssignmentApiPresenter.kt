package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.LocalDateTime
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
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.utils.AssignmentLanguageSupportException
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

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
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException("Assignment not found")
        val language = getLanguage(request.language) ?: throw NotFoundException("Language not found")
        if (request.code.isBlank()) {
            throw BadRequestException("The code is not blank")
        }
        val assignmentCode = getAssignmentCode(assignment, language.name)
        val code = getExecutableCode(assignmentCode, request.code)
        val response = repo.runCode(language.mime, code)
        return RunResponse(response.output, response.success ?: true)
    }

    private fun getExecutableCode(
        assignmentCode: AssignmentCode,
        requestCode: String,
    ): String = if (assignmentCode.injectable) {
        assignmentCode.starterCode.replace("{{code}}", requestCode)
    } else {
        requestCode
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
        if (request.code.isBlank()) {
            throw RuntimeException("Please do not submit an empty assignment")
        }
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException("Assignment not found")
        val attempt = repo.getLastStudentSubmissionForAssignment(assignment.id, request.studentId)?.attempt?.let {
            it + 1
        } ?: 1
        val isFinalAttempt = if (assignment.totalAttempts == 0) {
            false
        } else {
            attempt >= assignment.totalAttempts
        }
        val language = getLanguage(request.language) ?: throw NotFoundException("Language not found")
        val assignmentCode = getAssignmentCode(assignment, language.name)
        val solutionCode = if (assignmentCode.solutionVisibility == AssignmentCode.SolutionVisibility.onFinish) {
            assignmentCode.solutionCode
        } else {
            null
        }
        val attemptsRemaining = assignment.totalAttempts - attempt
        if (assignment.totalAttempts in 1 until attempt) {
            return SubmitResponse(
                output = "You have run out of attempts. \n ${assignment.failureMessage}",
                success = false,
                finalAttempt = true,
                solutionCode = solutionCode,
                gradePoints = assignment.points,
                attemptsRemaining = 0,
            )
        }

        if (assignmentCode.unitTest.isBlank()) {
            throw RuntimeException("No unit test created for this assignment")
        }
        val code = getExecutableCode(assignmentCode, request.code)
        val codeResponse = repo.testCode(language.mime, code, assignmentCode.unitTest)
        val languageRegex = assignmentCode.testFramework.parsingRegex
        val matches = languageRegex.findAll(codeResponse.output).toList()
        val currentTime = LocalDateTime.now()

        val response = if (matches.isNotEmpty()) {
            SubmitResponse(
                output = matches.first().value,
                success = false,
                finalAttempt = isFinalAttempt,
                solutionCode = solutionCode,
                gradePoints = assignment.points,
                attemptsRemaining = attemptsRemaining,
            )
        } else if (codeResponse.success == false) {
            SubmitResponse(
                output = codeResponse.output,
                success = false,
                finalAttempt = isFinalAttempt,
                solutionCode = solutionCode,
                gradePoints = assignment.points,
                attemptsRemaining = attemptsRemaining,
            )
        } else {
            SubmitResponse(
                output = assignment.successMessage,
                success = true,
                finalAttempt = isFinalAttempt,
                solutionCode = solutionCode,
                gradePoints = assignment.points,
                attemptsRemaining = attemptsRemaining,
            )
        }
        val submission = Submission(
            id = UUID.randomUUID(),
            code = request.code,
            assignmentId = assignment.id,
            languageId = language.id,
            studentId = request.studentId,
            attempt = attempt,
            feedback = response.output,
            createdAt = currentTime,
            correct = matches.isEmpty() && codeResponse.success != false,
            lastModifiedAt = currentTime,
        )
        repo.saveSubmission(submission)
        return response
    }

    override suspend fun get(request: GetAssignmentRequest): GetAssignmentResponse {
        val assignment = repo.getAssignment(request.referenceId) ?: throw NotFoundException()
        val submissions = repo.getStudentSubmissionsForAssignment(assignment.id, request.studentId)
        val assignmentCodes = assignment.assignmentCodes.map { assignmentCode ->
            val starterCode = if (assignmentCode.injectable) {
                ""
            } else {
                assignmentCode.starterCode
            }
            GetAssignmentResponse.AssignmentCode(
                displayName = assignmentCode.language.name,
                mime = assignmentCode.language.mime,
                starterCode = starterCode,
                primary = assignmentCode.primary,
                solutionCode = getSolutionCode(assignmentCode),
                url = assignmentCode.language.url,
                userCode = submissions.firstOrNull { it.languageId == assignmentCode.language.id }?.code
            )
        }.toMutableList()
        if (assignmentCodes.isEmpty()) {
            repo.getLanguages().forEachIndexed { index, language ->
                assignmentCodes.add(
                    GetAssignmentResponse.AssignmentCode(
                        displayName = language.name,
                        mime = language.mime,
                        starterCode = "",
                        primary = index == 0,
                        solutionCode = "",
                        url = language.url,
                        userCode = submissions.firstOrNull { it.languageId == language.id }?.code
                    )
                )
            }
        }
        if (assignment.blockId != request.blockId) {
            repo.updateAssignment(assignment.copy(blockId = request.blockId))
        }
        return GetAssignmentResponse(
            instructions = assignment.instructions,
            title = assignment.title,
            assignmentCodes = assignmentCodes,
            points = assignment.points,
            attemptsRemaining = assignment.totalAttempts - submissions.size,
        )
    }

    private fun getSolutionCode(assignmentCode: AssignmentCode): String {
        return if (assignmentCode.solutionVisibility == AssignmentCode.SolutionVisibility.always) {
            return assignmentCode.solutionCode
        } else {
            ""
        }
    }

    override suspend fun deleteAssignment(request: DeleteAssignmentRequest): DeleteAssignmentResponse {
        val uuid = request.id.toUuid() ?: throw Exception("Invalid assignment id")
        val assignment = repo.getAssignment(uuid) ?: throw Exception("Assignment could not be found")
        val updatedAssignment = assignment.copy(archived = false)
        val result = repo.updateAssignment(updatedAssignment)
        if (!result) {
            throw Exception("Unknown error")
        }
        return DeleteAssignmentResponse(assignment.courseId.toString())
    }

}

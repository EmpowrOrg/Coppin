package org.empowrco.coppin.assignment.presenters

import kotlinx.datetime.LocalDateTime
import org.apache.commons.text.StringEscapeUtils
import org.empowrco.coppin.assignment.backend.AssignmentPortalRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.portal.CodeListItem
import org.empowrco.coppin.utils.ellipsize
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.nonEmpty
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

interface AssignmentPortalPresenter {
    suspend fun getAssignments(): Result<GetAssignmentsResponse>
    suspend fun getAssignment(request: GetAssignmentRequest): Result<GetAssignmentPortalResponse>
    suspend fun updateAssignment(request: UpdateAssignmentPortalRequest): Result<UpdateAssignmentResponse>
    suspend fun getCode(id: String?, assignmentIdString: String): Result<GetCodeResponse>
    suspend fun saveCode(request: UpdateCodePortalRequest): Result<SaveCodeResponse>
    suspend fun createAssignment(request: CreateAssignmentPortalRequest): Result<CreateAssignmentResponse>
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
        val uuid = request.id?.toUuid() ?: return failure("Invalid id")
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
            assignmentCodes = emptyList(),
            title = request.title,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
        )
        repo.createAssignment(assignment)
        return CreateAssignmentResponse(id.toString()).toResult()
    }

    override suspend fun getAssignment(request: GetAssignmentRequest): Result<GetAssignmentPortalResponse> {
        if (request.id == null) {
            return GetAssignmentPortalResponse(
                title = null,
                successMessage = null,
                referenceId = null,
                failureMessage = null,
                attempts = null,
                id = null,
                instructions = null,
                codes = emptyList(),
            ).toResult()
        }
        val assignmentId = request.id.toUuid() ?: return failure("invalid id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("Assignment not found")
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
        return GetAssignmentPortalResponse(
            title = assignment.title,
            successMessage = StringEscapeUtils.escapeJava(assignment.successMessage),
            referenceId = "Reference Id: ${assignment.referenceId}",
            failureMessage = StringEscapeUtils.escapeJava(assignment.failureMessage),
            attempts = assignment.totalAttempts,
            id = assignment.id.toString(),
            instructions = StringEscapeUtils.escapeJava(assignment.instructions),
            codes = codes,
        ).toResult()
    }

    override suspend fun getCode(id: String?, assignmentIdString: String): Result<GetCodeResponse> {

        val assignmentId = assignmentIdString.toUuid() ?: return failure("Invalid assignment id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("No assignment found")
        val existingLanguageIds = repo.getAssignmentCodes(assignmentId).map { it.language.id }
        val selectableLanguages = repo.getLanguages().mapNotNull {
            if (existingLanguageIds.contains(it.id)) {
                return@mapNotNull null
            }
            GetCodeResponse.Language(
                name = it.name,
                id = it.id.toString(),
                url = it.url,
                mime = it.mime,
            )
        }
        if (id == null) {
            return GetCodeResponse(
                id = "",
                starterCode = "",
                solutionCode = "",
                assignmentId = assignment.id.toString(),
                unitTest = "",
                language = selectableLanguages.first(),
                primary = false,
                languages = selectableLanguages,

                ).toResult()
        }
        val assignmentCodeId = id.toUuid() ?: return failure("Invalid id")
        val code = repo.getCode(assignmentCodeId) ?: return failure("Code not found")
        return GetCodeResponse(
            id = code.id.toString(),
            primary = code.primary,
            starterCode = StringEscapeUtils.escapeJava(code.starterCode),
            solutionCode = StringEscapeUtils.escapeJava(code.solutionCode),
            assignmentId = code.assignmentId.toString(),
            unitTest = StringEscapeUtils.escapeJava(code.unitTest),
            language = GetCodeResponse.Language(
                name = code.language.name,
                id = code.language.id.toString(),
                mime = code.language.mime,
                url = code.language.url,
            ),
            languages = selectableLanguages,
        ).toResult()
    }

    override suspend fun saveCode(request: UpdateCodePortalRequest): Result<SaveCodeResponse> {
        val currentTime = LocalDateTime.now()
        val language = repo.getLanguage(request.languageMime) ?: return failure("Language not found")
        var primary = request.primary == "on"
        // new assignment code
        val assignmentId = request.assignmentId.toUuid() ?: return failure("No assignment found for this id")
        val codeIdString = request.id?.nonEmpty()
        if (codeIdString == null) {
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
                unitTest = request.unitTest ?: "",
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
            repo.saveCode(code)
        } else { // updating existing assignment code
            if (primary) {
                repo.deprimaryAssignmentCodes(assignmentId)
            }
            val assignmentCodeId = codeIdString.toUuid() ?: return failure("Invalid id")
            val code = repo.getCode(assignmentCodeId) ?: return failure("Code not found")
            val updatedCode = code.copy(
                starterCode = request.starterCode ?: "",
                solutionCode = request.solutionCode ?: "",
                language = language,
                primary = primary,
                unitTest = request.unitTest ?: "",
                lastModifiedAt = currentTime,
            )
            val result = repo.updateCode(updatedCode)
            if (!result) {
                return failure("Unknown error")
            }
        }
        return SaveCodeResponse.toResult()
    }

    override suspend fun deleteCode(id: String): Result<DeleteCodeResponse> {
        val uuid = id.toUuid() ?: return failure("Invalid code id")
        val code = repo.getCode(uuid) ?: return failure("Code not found")
        repo.deleteCode(uuid)
        if (code.primary) {
            val codes = repo.getAssignmentCodes(code.assignmentId)
            if (codes.isNotEmpty()) {
                repo.updateCode(codes.first().copy(primary = true))
            }
        }
        return DeleteCodeResponse.toResult()
    }
}

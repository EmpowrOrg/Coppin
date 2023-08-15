package org.empowrco.coppin.assignment.presenters

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import org.apache.commons.text.StringEscapeUtils
import org.empowrco.coppin.assignment.backend.AssignmentPortalRepository
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.utils.capitalize
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.nonEmpty
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.serialization.json
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

interface AssignmentPortalPresenter {
    suspend fun getAssignment(request: GetAssignmentRequest): Result<GetAssignmentPortalResponse>
    suspend fun updateAssignment(request: UpdateAssignmentPortalRequest): Result<UpdateAssignmentResponse>
    suspend fun getCode(request: GetCodeRequest): Result<GetCodeResponse>
    suspend fun updateCode(request: UpdateCodePortalRequest): Result<UpdateCodeResponse>
    suspend fun createAssignment(request: CreateAssignmentPortalRequest): Result<CreateAssignmentResponse>
    suspend fun deleteCode(request: DeleteCodeRequest): Result<DeleteCodeResponse>
    suspend fun archiveAssignment(request: ArchiveAssignmentRequest): Result<ArchiveAssignmentResponse>
    suspend fun getSubmission(request: GetSubmissionRequest): Result<GetSubmissionResponse>
    suspend fun generateAssignment(request: GenerateAssignmentRequest): Result<GenerateAssignmentResponse>
}

internal class RealAssignmentPortalPresenter(private val repo: AssignmentPortalRepository) : AssignmentPortalPresenter {

    override suspend fun updateAssignment(request: UpdateAssignmentPortalRequest): Result<UpdateAssignmentResponse> {
        val uuid = request.id?.toUuid() ?: return failure("Invalid id")
        val assignment = repo.getAssignment(uuid) ?: return failure("Assignment not found")
        val currentTime = LocalDateTime.now()
        if (request.title.isBlank()) {
            return failure("Please specify a title")
        } else if (request.instructions.isBlank()) {
            return failure("Please include instructions")
        } else if (request.successMessage.isBlank()) {
            return failure("Please include a success message")
        } else if (request.failureMessage.isBlank()) {
            return failure("Please include a failure message")
        }
        val subjectId = request.subject.toUuid() ?: return failure("Invalid Subject Selected")
        val subject = repo.getSubject(subjectId) ?: return failure("Subject not found")
        val updatedAssignment = assignment.copy(
            failureMessage = request.failureMessage,
            successMessage = request.successMessage,
            instructions = request.instructions,
            totalAttempts = request.totalAttempts,
            title = request.title,
            points = request.points.toDouble(),
            subject = subject,
            lastModifiedAt = currentTime,
        )
        val result = repo.updateAssignment(updatedAssignment)
        if (!result) {
            return failure("There was an error updating the assignment")
        }
        return UpdateAssignmentResponse(updatedAssignment.courseId.toString()).toResult()
    }

    override suspend fun createAssignment(request: CreateAssignmentPortalRequest): Result<CreateAssignmentResponse> {
        val currentTime = LocalDateTime.now()
        val id = UUID.randomUUID()
        if (request.title.isBlank()) {
            return failure("Please specify a title")
        } else if (request.instructions.isBlank()) {
            return failure("Please include instructions")
        } else if (request.successMessage.isBlank()) {
            return failure("Please include a success message")
        } else if (request.failureMessage.isBlank()) {
            return failure("Please include a failure message")
        } else if (request.totalAttempts.toIntOrNull() == null) {
            return failure("Please specify total attempts")
        } else if (request.points.toDoubleOrNull() == null) {
            return failure("Please specify points")
        }
        val courseId = request.courseId.toUuid() ?: return failure("Invalid course id")
        val course = repo.getCourse(courseId) ?: return failure("Course not found")
        val subjectId = request.subjectId.toUuid() ?: return failure("Invalid Subject Selected")
        val subject = repo.getSubject(subjectId) ?: return failure("Subject not found")
        val referenceName = "${course.edxId}-${request.title.trim().replace("\\p{Zs}+".toRegex(), "-")}"
        val referenceCount = repo.assignmentsWithReferenceStartingWithCount(referenceName)
        val referenceId = if (referenceCount > 0) {
            "$referenceName-$referenceCount"
        } else {
            referenceName
        }
        val assignment = Assignment(
            id = id,
            failureMessage = request.failureMessage,
            successMessage = request.successMessage,
            instructions = request.instructions,
            totalAttempts = request.totalAttempts.toInt(),
            referenceId = referenceId,
            assignmentCodes = emptyList(),
            title = request.title,
            blockId = null,
            archived = false,
            courseId = course.id,
            createdAt = currentTime,
            lastModifiedAt = currentTime,
            subject = subject,
            points = request.points.toDouble(),
        )
        repo.createAssignment(assignment)
        return CreateAssignmentResponse(id.toString()).toResult()
    }

    override suspend fun getAssignment(request: GetAssignmentRequest): Result<GetAssignmentPortalResponse> {
        val courseId = request.courseId.toUuid() ?: return failure("Invalid course id")
        val course = repo.getCourse(courseId) ?: return failure("Course not found")
        val subjects = repo.getSubjectsForCourse(courseId)
        if (request.id == null) {
            return GetAssignmentPortalResponse(
                title = null,
                successMessage = null,
                referenceId = null,
                failureMessage = null,
                attempts = null,
                id = null,
                courseId = request.courseId,
                instructions = null,
                codes = emptyList(),
                subjects = subjects.map {
                    GetAssignmentPortalResponse.Subject(
                        id = it.id.toString(),
                        name = it.name,
                    )
                },
                subjectId = null,
                submissions = emptyList(),
                points = null,
                courseName = course.title,
                userId = request.userId,
                showGenerate = !System.getenv("OPEN_AI_MODEL").isNullOrBlank(),
            ).toResult()
        }
        val assignmentId = request.id.toUuid() ?: return failure("invalid id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("Assignment not found")
        val codes = assignment.assignmentCodes.map {
            GetAssignmentPortalResponse.Code(
                id = it.id.toString(),
                language = it.language.name,
                primary = it.primary.toString().capitalize(),
                hasSolution = it.solutionCode.isNotBlank().toString().capitalize(),
                hasStarter = it.starterCode.isNotBlank().toString().capitalize(),
                injectable = it.injectable.toString().capitalize(),
                assignmentId = assignment.id.toString(),
            )
        }
        val studentSubmissions = repo.getLatestStudentSubmissionForAssignment(assignmentId)
        return GetAssignmentPortalResponse(
            title = assignment.title,
            successMessage = assignment.successMessage,
            referenceId = assignment.referenceId,
            courseId = assignment.courseId.toString(),
            failureMessage = assignment.failureMessage,
            attempts = assignment.totalAttempts,
            id = assignment.id.toString(),
            instructions = assignment.instructions,
            codes = codes,
            subjectId = assignment.subject.id.toString(),
            subjects = subjects.map {
                GetAssignmentPortalResponse.Subject(
                    id = it.id.toString(),
                    name = it.name,
                )
            },
            points = assignment.points.toInt(),
            courseName = course.title,
            submissions = studentSubmissions.map {
                val language = repo.getLanguage(it.languageId)
                GetAssignmentPortalResponse.Submission(
                    id = it.id.toString(),
                    success = it.correct.toString(),
                    numberOfAttempts = it.attempt,
                    username = it.studentId,
                    language = language?.name ?: "Unknown"
                )
            },
            userId = request.userId,
            showGenerate = !System.getenv("OPEN_AI_MODEL").isNullOrBlank(),
        ).toResult()
    }

    override suspend fun getCode(request: GetCodeRequest): Result<GetCodeResponse> {
        val assignmentId = request.assignmentId.toUuid() ?: return failure("Invalid assignment id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("No assignment found")
        val existingLanguageIds = repo.getAssignmentCodes(assignmentId).map { it.language.id }
        val course = repo.getCourse(assignment.courseId) ?: return failure("Course not found")
        val selectableLanguages = repo.getLanguages().mapNotNull {
            if (existingLanguageIds.contains(it.id)) {
                return@mapNotNull null
            }
            GetCodeResponse.Language(
                name = it.name,
                id = it.id.toString(),
                url = it.url,
                mime = it.mime,
                selected = false,
            )
        }.sortedBy { it.name }.toMutableList()
        if (request.id == null) {
            return GetCodeResponse(
                id = null,
                starterCode = "",
                solutionCode = "",
                assignmentId = assignment.id.toString(),
                unitTest = "",
                injectable = false,
                courseId = assignment.courseId.toString(),
                language = selectableLanguages.first(),
                primary = existingLanguageIds.isEmpty(), //Should be primary if there are now existing languages
                languages = selectableLanguages,
                solutionVisibility = AssignmentCode.SolutionVisibility.onFinish.name,
                assignmentName = assignment.title,
                courseName = course.title,
            ).toResult()
        }
        val assignmentCodeId = request.id.toUuid() ?: return failure("Invalid id")
        val code = repo.getCode(assignmentCodeId) ?: return failure("Code not found")
        selectableLanguages.add(
            GetCodeResponse.Language(
                name = code.language.name,
                id = code.language.id.toString(),
                url = code.language.url,
                mime = code.language.mime,
                selected = true,
            )
        )
        return GetCodeResponse(
            id = code.id.toString(),
            primary = code.primary,
            starterCode = StringEscapeUtils.escapeJava(code.starterCode),
            solutionCode = StringEscapeUtils.escapeJava(code.solutionCode),
            assignmentId = code.assignmentId.toString(),
            courseId = assignment.courseId.toString(),
            unitTest = StringEscapeUtils.escapeJava(code.unitTest),
            language = GetCodeResponse.Language(
                name = code.language.name,
                id = code.language.id.toString(),
                mime = code.language.mime,
                url = code.language.url,
                selected = true,
            ),
            injectable = code.injectable,
            languages = selectableLanguages,
            solutionVisibility = code.solutionVisibility.name,
            assignmentName = assignment.title,
            courseName = course.title,
        ).toResult()
    }

    override suspend fun updateCode(request: UpdateCodePortalRequest): Result<UpdateCodeResponse> {
        val currentTime = LocalDateTime.now()
        val language = repo.getLanguage(request.languageMime) ?: return failure("Language not found")
        var primary = request.primary == "on"
        val injectable = request.injectable == "on"
        if (injectable && (request.starterCode.isNullOrBlank() || !request.starterCode.contains("{{code}}"))) {
            return failure("Injectable assignments must contain {{code}} placeholder")
        } else if (request.solutionCode.isBlank()) {
            return failure("You must specify a solution")
        }
        // new assignment code
        val assignmentId = request.assignmentId.toUuid() ?: return failure("Invalid assignment id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("No assignment found")
        val codeIdString = request.id?.nonEmpty()
        val solutionVisibility = try {
            AssignmentCode.SolutionVisibility.valueOf(request.solutionVisibility)
        } catch (ex: Exception) {
            return failure(ex.localizedMessage)
        }
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
                solutionCode = request.solutionCode,
                unitTest = request.unitTest ?: "",
                injectable = injectable,
                solutionVisibility = solutionVisibility,
                createdAt = currentTime,
                lastModifiedAt = currentTime,
            )
            repo.saveCode(code)
            repo.updateAssignment(assignment)
        } else { // updating existing assignment code
            if (primary) {
                repo.deprimaryAssignmentCodes(assignment)
            }
            val assignmentCodeId = codeIdString.toUuid() ?: return failure("Invalid id")
            val code = repo.getCode(assignmentCodeId) ?: return failure("Code not found")
            val updatedCode = code.copy(
                starterCode = request.starterCode ?: "",
                solutionCode = request.solutionCode ?: "",
                language = language,
                primary = primary,
                injectable = injectable,
                solutionVisibility = solutionVisibility,
                unitTest = request.unitTest ?: "",
                lastModifiedAt = currentTime,
            )
            val result = repo.updateCode(updatedCode)
            if (!result) {
                return failure("Unknown error")
            }
            repo.updateAssignment(assignment)
        }
        return UpdateCodeResponse(assignment.courseId.toString()).toResult()
    }

    override suspend fun deleteCode(request: DeleteCodeRequest): Result<DeleteCodeResponse> {
        val uuid = request.id.toUuid() ?: return failure("Invalid code id")
        val code = repo.getCode(uuid) ?: return failure("Code not found")
        val assignment = repo.getAssignment(code.assignmentId) ?: return failure("Assignment not found")
        repo.deleteCode(code)
        if (code.primary) {
            val codes = repo.getAssignmentCodes(code.assignmentId)
            if (codes.isNotEmpty()) {
                repo.updateCode(codes.first().copy(primary = true))
            }
        }
        repo.updateAssignment(assignment)
        return DeleteCodeResponse(assignment.courseId.toString()).toResult()
    }

    override suspend fun archiveAssignment(request: ArchiveAssignmentRequest): Result<ArchiveAssignmentResponse> {
        val uuid = request.id.toUuid() ?: return failure("Invalid assignment id")
        val assignment = repo.getAssignment(uuid) ?: return failure("Assignment could not be found")
        val updatedAssignment = assignment.copy(archived = false)
        val result = repo.updateAssignment(updatedAssignment)
        if (!result) {
            return failure("Unknown error")
        }
        return ArchiveAssignmentResponse(assignment.courseId.toString()).toResult()
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun getSubmission(request: GetSubmissionRequest): Result<GetSubmissionResponse> {
        val assignmentId = request.assignmentId.toUuid() ?: return failure("Invalid assignment id")
        val assignment = repo.getAssignment(assignmentId) ?: return failure("Assignment not found")
        val course = repo.getCourse(assignment.courseId) ?: return failure("Course not found")
        val submissions =
            repo.getStudentSubmissionsForAssignment(assignmentId, request.studentId).sortedBy { it.attempt }
        val submissionPresentables = submissions.map {
            val language = repo.getLanguage(it.languageId) ?: return failure("Invalid language for Submission")
            GetSubmissionResponse.Submission(
                code = StringEscapeUtils.escapeJava(it.code),
                attempt = it.attempt,
                language = GetSubmissionResponse.Language(
                    url = language.url,
                    name = language.name,
                    mime = language.mime,
                )
            )
        }
        return GetSubmissionResponse(
            studentId = request.studentId,
            submissions = submissionPresentables,
            assignment = assignment.title,
            submissionsJson = StringEscapeUtils.escapeJson(
                json.encodeToString(
                    ListSerializer(GetSubmissionResponse.Submission::class.serializer()),
                    submissionPresentables
                ),
            ),
            assignmentId = assignment.id.toString(),
            courseName = course.title,
            courseId = course.id.toString(),
        ).toResult()
    }

    override suspend fun generateAssignment(request: GenerateAssignmentRequest): Result<GenerateAssignmentResponse> {
        if (request.prompt.isBlank()) {
            return failure("You must specify a prompt")
        } else if (request.userId.isBlank()) {
            return failure("Please try logging in again.")
        }
        val response = repo.generateAssignment(request.prompt, request.userId)
        if (response.response.isNullOrBlank()) {
            return failure(response.stopReason ?: "Unknown error")
        }
        return GenerateAssignmentResponse(
            instructions = response.response!!
        ).toResult()
    }
}

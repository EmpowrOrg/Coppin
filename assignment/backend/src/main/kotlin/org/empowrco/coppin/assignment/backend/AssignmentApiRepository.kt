package org.empowrco.coppin.assignment.backend

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.LanguagesSource
import org.empowrco.coppin.sources.SubmissionSource
import org.empowrco.coppin.utils.logs.logDebug
import java.util.UUID
import java.util.concurrent.TimeUnit

interface AssignmentApiRepository {
    suspend fun getAssignment(referenceId: String): Assignment?
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun getLanguage(id: UUID): Language?
    suspend fun getLanguages(): List<Language>
    suspend fun runCode(language: String, code: String): AssignmentCodeResponse
    suspend fun testCode(language: String, code: String, tests: String): AssignmentCodeResponse
    suspend fun deleteAssignment(id: UUID): Boolean
    suspend fun saveSubmission(submission: Submission)
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getStudentSubmissionsForAssignment(assignmentID: UUID, studentId: String): List<Submission>

}

internal class RealAssignmentApiRepository(
    private val assignmentSource: AssignmentSource,
    private val languagesSource: LanguagesSource,
    private val submissionSource: SubmissionSource,
) : AssignmentApiRepository {
    @OptIn(ExperimentalSerializationApi::class)
    val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(org.empowrco.coppin.utils.serialization.json)
        }
        install(HttpTimeout) {
            val timeout = TimeUnit.MINUTES.toMillis(1)
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
        }
    }

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignmentSource.getAssignment(id)
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        return assignmentSource.updateAssignment(assignment)
    }

    override suspend fun getLanguages(): List<Language> {
        return languagesSource.getLanguages()
    }

    override suspend fun getAssignment(referenceId: String): Assignment? {
        return assignmentSource.getAssignmentByReferenceId(referenceId)
    }

    override suspend fun getLanguage(id: UUID): Language? {
        return languagesSource.getLanguage(id)
    }

    override suspend fun runCode(language: String, code: String): AssignmentCodeResponse {
        return executeRequest(
            "run", JsonObject(
                mapOf(
                    "language" to JsonPrimitive(language),
                    "code" to JsonPrimitive(code),
                )
            )
        )
    }

    override suspend fun testCode(language: String, code: String, tests: String): AssignmentCodeResponse {
        return executeRequest(
            "test", JsonObject(
                mapOf(
                    "language" to JsonPrimitive(language),
                    "code" to JsonPrimitive(code),
                    "unitTests" to JsonPrimitive(tests),
                )
            )
        )
    }


    override suspend fun deleteAssignment(id: UUID): Boolean {
        return assignmentSource.deleteAssignment(id)
    }

    override suspend fun saveSubmission(submission: Submission) {
        submissionSource.saveSubmission(submission)
    }

    override suspend fun getStudentSubmissionsForAssignment(assignmentID: UUID, studentId: String): List<Submission> {
        return submissionSource.getSubmissionsForAssignment(assignmentID, studentId)
    }

    private suspend fun executeRequest(
        path: String,
        body: JsonObject,
    ): AssignmentCodeResponse {
        val url = System.getenv("DOCTOR_URL")
        val response = client.post("$url$path") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        val code = response.body<AssignmentCodeResponse>()
        logDebug(code.toString())
        return code
    }
}

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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.models.responses.AiResponse
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.LanguagesSource
import org.empowrco.coppin.sources.OpenAiSource
import org.empowrco.coppin.sources.SettingsSource
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
    suspend fun testCode(language: String, code: String, tests: String, framework: String, commands: List<String>): AssignmentCodeResponse
    suspend fun deleteAssignment(assignment: Assignment): Boolean
    suspend fun saveSubmission(submission: Submission)
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getStudentSubmissionsForAssignment(assignmentID: UUID, studentId: String): List<Submission>
    suspend fun getLastStudentSubmissionForAssignment(assignmentID: UUID, studentId: String): Submission?
    suspend fun getAssignments(courseId: UUID): List<Assignment>
    suspend fun getAiFeedback(
        solution: String,
        instructions: String,
        submission: String,
        user: String,
        language: String,
        error: String?,
    ): AiResponse

}

internal class RealAssignmentApiRepository(
    private val assignmentSource: AssignmentSource,
    private val languagesSource: LanguagesSource,
    private val submissionSource: SubmissionSource,
    private val settingsSource: SettingsSource,
    private val openAiSource: OpenAiSource,
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

    override suspend fun getLastStudentSubmissionForAssignment(assignmentID: UUID, studentId: String): Submission? {
        return submissionSource.getLastStudentSubmissionForAssignment(assignmentID, studentId)
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

    override suspend fun getAiFeedback(
        solution: String,
        instructions: String,
        submission: String,
        user: String,
        language: String,
        error: String?,
    ): AiResponse {
        var query = """
            You are a helpful teaching assistant reviewing a beginner-level $language programming assignment. 
            The student has submitted their code${if (solution.isNotBlank()) ", and you also have access to the reference solution." else "."} 
            Do not reveal or provide any part of the reference solution. 
            Instead, provide supportive, detailed feedback on what the student did wrong and how they might improve. 
            Do not show the correct or fixed code; just guide the student toward discovering it themselves.
Student Submission:

$submission

${
            if (!error.isNullOrBlank()) {
                "We have an output when we ran the program. Please only pay attention to errors and ignore warning/debug output.\nOutput received when running and testing program: \n $error"
            } else {
                ""
            }
        }
${
            if (solution.isNotBlank()) "Reference Solution (for instructor use only – do not reveal directly):\n" +
                    "\n" +
                    solution else ""
        }


            Output Requirements
            	1.	Produce your feedback as Markdown text.
            	2.	Maintain a supportive and encouraging tone.
            	3.	Identify syntax errors, logic errors, or conceptual misunderstandings.
            	4.	Provide hints on how to fix these issues, without revealing the correct code.
            	5.	Suggest official $language resources (e.g., $language documentation) for further study. If student's error revolves around a lack of understanding on an instruction, emphasize the relevant instructions.
            	6.	Focus on guiding the student to understand the underlying concepts.

            Markdown Structure Example

            Use the following Markdown headings and lists to structure your output:

            ## Feedback

            A short supportive statement acknowledging the student’s effort.

            ### Errors
            1. **Type**: e.g., syntax / logic / etc.
               - **Description**: A short explanation of what went wrong.
               - **Hint**: Guidance on how the student might fix or rethink that issue, without giving the direct solution.

            2. **Type**: ...
               - **Description**: ...
               - **Hint**: ...

            ### Conceptual Guidance
            An optional paragraph or two explaining which key $language concept(s) the student should revisit.

            ### Next Steps
            Specific actions or questions the student can explore to self-correct or deepen their understanding.

            ### Resources
            An optional bulleted list of official $language or other reputable documentation links.

            Example Markdown Output

            Below is an example of what your final Markdown output might look like for a student who made simple syntax and logic errors in $language. Note that this example is not referencing real code—it’s just a template to illustrate the format:

            ## Feedback

            Excellent start! It’s clear you put effort into learning ${language}’s syntax.

            ### Errors
            1. **Type**: Syntax
               - **Description**: You used a misspelled print function (`prnit`), causing a compile-time error.
               - **Hint**: Revisit your spelling for built-in $language functions. Accurate keywords and function names are crucial.

            2. **Type**: Logic
               - **Description**: Your loop runs indefinitely because the loop variable never changes inside the loop.
               - **Hint**: Consider how modifying the loop variable each iteration helps the loop eventually terminate.

            ### Conceptual Guidance
            Loops in $language rely on certain conditions to start and end. Make sure you understand how those conditions are updated on each pass.

            ### Next Steps
            - Review how to update variables within a loop to ensure the stopping condition will be reached.
            - After making changes, test your code with different values to confirm the loop behaves as expected.

            ### Resources
            - [$language Language Guide: Control Flow](https://docs.$language.org/$language-book/LanguageGuide/ControlFlow.html)
            - [$language Documentation](https://developer.apple.com/documentation/$language)

            Use this structure as a guide. The exact content will naturally vary based on the student’s specific code submission. Remember, do not provide the exact corrected code—only guide them to it.
                       
            Instructions provided to the student: 
            $instructions
            
            Student Submission: 
            $submission
        """.trimIndent()
        return openAiSource.rawPrompt(query, user)
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

    override suspend fun testCode(language: String, code: String, tests: String, framework: String, commands: List<String>): AssignmentCodeResponse {
        return executeRequest(
            "test", JsonObject(
                mapOf(
                    "language" to JsonPrimitive(language),
                    "code" to JsonPrimitive(code),
                    "unitTests" to JsonPrimitive(tests),
                    "data" to buildJsonObject {
                        put("framework", JsonPrimitive(framework))
                        put("commands", JsonArray(commands.map { JsonPrimitive(it) }))
                    }
                )
            )
        )
    }


    override suspend fun deleteAssignment(assignment: Assignment): Boolean {
        return assignmentSource.deleteAssignment(assignment)
    }

    override suspend fun saveSubmission(submission: Submission) {
        submissionSource.saveSubmission(submission)
    }

    override suspend fun getStudentSubmissionsForAssignment(assignmentID: UUID, studentId: String): List<Submission> {
        return submissionSource.getSubmissionsForAssignment(assignmentID, studentId)
    }

    override suspend fun getAssignments(courseId: UUID): List<Assignment> {
        return assignmentSource.getAssignmentsForCourse(courseId)
    }

    private suspend fun executeRequest(
        path: String,
        body: JsonObject,
    ): AssignmentCodeResponse {
        val url = settingsSource.getOrgSettings()?.doctorUrl
            ?: throw Exception("Org Settings Not Found. This should never happen")
        val response = client.post("$url$path") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        val code = response.body<AssignmentCodeResponse>()
        logDebug(code.toString())
        return code
    }
}

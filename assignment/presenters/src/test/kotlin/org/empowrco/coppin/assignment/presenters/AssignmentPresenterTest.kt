package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.assignment.backend.fakes.FakeAssignmentRepo
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Feedback
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.utils.diff.fakes.FakeDiffUtil
import org.empowrco.coppin.utils.now
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class AssignmentPresenterTest {
    private val repo = FakeAssignmentRepo()
    private val diffUtil = FakeDiffUtil()
    private val presenter = RealAssignmentPresenter(repo, diffUtil)

    @AfterTest
    fun teardown() {
        repo.languages.clear()
        repo.assignments.clear()
    }

    @Test
    fun submitAssignmentNotFound(): Unit = runBlocking {
        assertFailsWith<NotFoundException> {
            presenter.submit(submitRequest)
        }
    }

    @Test
    fun submitTooManyAttempts() = runBlocking {
        repo.assignments.add(assigment)
        val request = submitRequest.copy(attempt = assigment.totalAttempts + 1)

        val response = presenter.submit(request)
        assertEquals(
            response, SubmitResponse(
                output = assigment.failureMessage,
                feedback = "",
                success = false,
                expectedOutput = assigment.expectedOutput,
                finalAttempt = true,
                diff = null,
            )
        )
    }

    @Test
    fun submitErrorWithFeedback(): Unit = runBlocking {
        repo.assignments.add(assigment)
        val response = presenter.submit(submitRequest.copy(attempt = 2, output = "failure"))
        assertEquals(
            response, SubmitResponse(
                output = "failure",
                feedback = "feedback3",
                success = false,
                expectedOutput = assigment.expectedOutput,
                finalAttempt = false,
                diff = null,
            )
        )
    }

    @Test
    fun submitSuccessWithFeedback(): Unit = runBlocking {
        repo.assignments.add(assigment)
        val response = presenter.submit(submitRequest.copy(attempt = 1, output = "code"))
        assertEquals(
            response, SubmitResponse(
                output = "code",
                feedback = "feedback1",
                success = false,
                expectedOutput = assigment.expectedOutput,
                finalAttempt = false,
                diff = null,
            )
        )
    }

    @Test
    fun submitSuccess(): Unit = runBlocking {
        repo.assignments.add(assigment)
        val response = presenter.submit(submitRequest.copy(attempt = 1, output = "Hello, World"))
        with(response) {
            assertEquals(output, assigment.expectedOutput)
            assertEquals(feedback, assigment.successMessage)
            assertEquals(success, true)
        }
        assertEquals(
            response, SubmitResponse(
                output = assigment.expectedOutput,
                feedback = assigment.successMessage,
                success = true,
                expectedOutput = assigment.expectedOutput,
                finalAttempt = false,
                diff = null,
            )
        )
    }

    @Test
    fun create(): Unit = runBlocking {
        val language = Language(
            id = UUID.randomUUID(),
            name = "name",
            mime = "mime",
            createdAt = LocalDateTime.now(),
            lastModifiedAt = LocalDateTime.now(),
        )
        repo.languages.add(language)
        val response = presenter.create(
            CreateAssignmentRequest(
                instructions = assigment.instructions,
                totalAttempts = assigment.totalAttempts,
                failureMessage = assigment.failureMessage,
                solution = assigment.solution,
                successMessage = assigment.successMessage,
                referenceId = assigment.referenceId,
                expectedOutput = assigment.expectedOutput,
                feedback = assigment.feedback.map {
                    CreateAssignmentRequest.Feedback(
                        attempt = it.attempt,
                        feedback = it.feedback,
                        regex = it.regexMatcher,
                    )
                },
                starterCodes = listOf(
                    CreateAssignmentRequest.StarterCode(
                        languageId = language.id.toString(),
                        primary = true,
                        code = "code",
                    )
                ),
                title = "title",
            )
        )
        assertIs<CreateAssignmentResponse>(response)
        assertEquals(repo.assignments.size, 1)
    }

    companion object {
        val submitRequest = SubmitRequest(
            attempt = 0,
            code = "code",
            referenceId = "reference",
            language = "lang",
            email = "email",
            executeSuccess = true,
            output = "output",
        )
        val assigment = Assignment(
            id = UUID.randomUUID(),
            referenceId = "reference",
            expectedOutput = "Hello, World",
            instructions = "instructions",
            lastModifiedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            successMessage = "success",
            failureMessage = "failure",
            solution = "solution",
            totalAttempts = 4,
            feedback = listOf(
                Feedback(
                    id = UUID.randomUUID(),
                    regexMatcher = "code",
                    createdAt = LocalDateTime.now(),
                    lastModifiedAt = LocalDateTime.now(),
                    feedback = "feedback1",
                    attempt = 1,
                    assignmentId = UUID.randomUUID()
                ),
                Feedback(
                    id = UUID.randomUUID(),
                    regexMatcher = "regex",
                    createdAt = LocalDateTime.now(),
                    lastModifiedAt = LocalDateTime.now(),
                    feedback = "feedback2",
                    attempt = 2,
                    assignmentId = UUID.randomUUID()
                ),
                Feedback(
                    id = UUID.randomUUID(),
                    regexMatcher = "",
                    createdAt = LocalDateTime.now(),
                    lastModifiedAt = LocalDateTime.now(),
                    feedback = "feedback3",
                    attempt = 2,
                    assignmentId = UUID.randomUUID()
                ),
                Feedback(
                    id = UUID.randomUUID(),
                    regexMatcher = "hello",
                    createdAt = LocalDateTime.now(),
                    lastModifiedAt = LocalDateTime.now(),
                    feedback = "feedback4",
                    attempt = 3,
                    assignmentId = UUID.randomUUID()
                ),
            ),
            assignmentCodes = emptyList(),
            title = "title",
        )
    }

}

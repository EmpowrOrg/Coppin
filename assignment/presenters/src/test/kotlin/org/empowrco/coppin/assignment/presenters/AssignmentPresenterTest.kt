package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.assignment.backend.fakes.FakeAssignmentRepo
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Feedback
import org.empowrco.coppin.models.NoValidExecutor
import org.empowrco.coppin.models.Success
import org.empowrco.coppin.utils.UnsupportedLanguage
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
        repo.executorResponses.clear()
        repo.assignments.clear()
    }

    @Test
    fun runSuccess(): Unit = runBlocking {
        val output = "Hello, World"
        repo.assignments.add(assigment)
        repo.executorResponses.add(Success(output))
        diffUtil.diffHtmls.put(Pair(output, assigment.expectedOutput), "diff")
        val response = presenter.run(
            RunRequest(
                code = "a",
                language = "",
                referenceId = assigment.referenceId
            )
        )
        assertEquals(
            response, RunResponse(
                expectedOutput = assigment.expectedOutput,
                output = "Hello, World",
                diff = "diff",
                success = true,
            )
        )
    }

    @Test
    fun runUnsupportedLanguage(): Unit = runBlocking {
        repo.executorResponses.add(NoValidExecutor("lang"))
        repo.assignments.add(assigment)
        val exception = assertFailsWith<UnsupportedLanguage> {
            presenter.run(
                RunRequest(
                    code = "a",
                    language = "",
                    referenceId = assigment.referenceId
                )
            )
        }
        assertEquals(exception.message, "The server does not support lang")
    }

    @Test
    fun runError(): Unit = runBlocking {
        repo.executorResponses.add(Error("error"))
        repo.assignments.add(assigment)
        val exception = assertFailsWith<Error> {
            presenter.run(
                RunRequest(
                    code = "a",
                    language = "",
                    referenceId = assigment.referenceId,
                )
            )
        }
        assertEquals(exception.message, "error")
    }

    @Test
    fun runAssignmentNotFound(): Unit = runBlocking {
        repo.assignments.add(assigment.copy(referenceId = "b"))
        repo.executorResponses.add(Success("code"))
        assertFailsWith<NotFoundException> {
            presenter.run(
                RunRequest(
                    referenceId = "a",
                    code = "",
                    language = "",
                )
            )
        }
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
    fun submitNoValidExecutor(): Unit = runBlocking {
        repo.assignments.add(assigment)
        repo.executorResponses.add(NoValidExecutor("lang"))
        assertFailsWith<UnsupportedLanguage> {
            presenter.submit(submitRequest)
        }
    }

    @Test
    fun submitErrorNoFeedback(): Unit = runBlocking {
        repo.assignments.add(assigment.copy(feedback = listOf()))
        repo.executorResponses.add(Error("failure"))
        val response = presenter.submit(submitRequest)
        with(response) {
            assertEquals(output, assigment.failureMessage)
            assertEquals(feedback, "")
            assertEquals(success, false)
        }
    }

    @Test
    fun submitErrorWithFeedback(): Unit = runBlocking {
        repo.assignments.add(assigment)
        repo.executorResponses.add(Error("failure"))
        val response = presenter.submit(submitRequest.copy(attempt = 2))
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
        repo.executorResponses.add(Success("code"))
        val response = presenter.submit(submitRequest.copy(attempt = 1))
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
        repo.executorResponses.add(Success(assigment.expectedOutput))
        val response = presenter.submit(submitRequest.copy(attempt = 1))
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
                ),
                Feedback(
                    id = UUID.randomUUID(),
                    regexMatcher = "regex",
                    createdAt = LocalDateTime.now(),
                    lastModifiedAt = LocalDateTime.now(),
                    feedback = "feedback2",
                    attempt = 2,
                ),
                Feedback(
                    id = UUID.randomUUID(),
                    regexMatcher = "",
                    createdAt = LocalDateTime.now(),
                    lastModifiedAt = LocalDateTime.now(),
                    feedback = "feedback3",
                    attempt = 2,
                ),
                Feedback(
                    id = UUID.randomUUID(),
                    regexMatcher = "hello",
                    createdAt = LocalDateTime.now(),
                    lastModifiedAt = LocalDateTime.now(),
                    feedback = "feedback4",
                    attempt = 3,
                ),
            )
        )
    }

}

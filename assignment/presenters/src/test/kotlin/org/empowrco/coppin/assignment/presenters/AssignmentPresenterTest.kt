package org.empowrco.coppin.assignment.presenters

import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.assignment.backend.AssignmentCodeResponse
import org.empowrco.coppin.assignment.backend.fakes.FakeAssignmentRepoApi
import org.empowrco.coppin.assignment.presenters.RequestApi.SubmitRequest
import org.empowrco.coppin.assignment.presenters.ResponseApi.SubmitResponse
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Feedback
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.utils.now
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AssignmentPresenterTest {
    private val repo = FakeAssignmentRepoApi()
    private val presenter = RealAssignmentApiPresenter(repo)

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
            response, ResponseApi.SubmitResponse(
                output = assigment.failureMessage,
                feedback = "",
                success = false,
                finalAttempt = true,
                diff = null,
            )
        )
    }

    @Test
    fun submitErrorWithFeedback(): Unit = runBlocking {
        repo.assignments.add(assigment)
        repo.codeResponses.add(
            AssignmentCodeResponse(
                success = false,
                output = "failure"
            )
        )
        val response = presenter.submit(submitRequest.copy(attempt = 2))
        assertEquals(
            response, SubmitResponse(
                output = "failure",
                feedback = "feedback3",
                success = false,
                finalAttempt = false,
                diff = null,
            )
        )
    }

    @Test
    fun submitSuccessWithFeedback(): Unit = runBlocking {
        repo.assignments.add(assigment)
        repo.codeResponses.add(
            AssignmentCodeResponse(
                success = false,
                output = "code"
            )
        )
        val response = presenter.submit(submitRequest.copy(attempt = 1))
        assertEquals(
            response, SubmitResponse(
                output = "code",
                feedback = "feedback1",
                success = false,
                finalAttempt = false,
                diff = null,
            )
        )
    }

    @Test
    fun submitSuccess(): Unit = runBlocking {
        repo.assignments.add(assigment)
        repo.codeResponses.add(
            AssignmentCodeResponse(
                success = true,
                output = "Hello, World"
            )
        )
        val response = presenter.submit(submitRequest.copy(attempt = 1))
        with(response) {
            assertEquals(feedback, assigment.successMessage)
            assertEquals(success, true)
        }
        assertEquals(
            response, SubmitResponse(
                feedback = assigment.successMessage,
                success = true,
                finalAttempt = false,
                diff = null,
                output = "success"
            )
        )
    }


    companion object {
        val submitRequest = SubmitRequest(
            attempt = 0,
            code = "code",
            referenceId = "reference",
            language = "lang",
            email = "email",
        )
        val lang = Language(
            id = UUID.randomUUID(),
            lastModifiedAt = LocalDateTime.now(),
            mime = "lang-mime",
            name = "lang",
            url = "lang-url",
            createdAt = LocalDateTime.now(),
        )
        val assignmentId = UUID.randomUUID()
        val assignmentCode = AssignmentCode(
            id = UUID.randomUUID(),
            assignmentId = assignmentId,
            starterCode = "starter-code",
            solutionCode = "solution-code",
            language = lang,
            primary = true,
            unitTest = "unit-test",
            lastModifiedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
        )

        val assigment = Assignment(
            id = assignmentId,
            referenceId = "reference",
            instructions = "instructions",
            lastModifiedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            successMessage = "success",
            failureMessage = "failure",
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
            assignmentCodes = listOf(assignmentCode),
            title = "title",
        )
    }

}

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
            response, SubmitResponse(
                output = assigment.failureMessage,
                success = false,
                finalAttempt = true,
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
        repo.languages.add(lang)
        val response = presenter.submit(submitRequest.copy(attempt = 2))
        assertEquals(
            response, SubmitResponse(
                output = "failure",
                success = false,
                finalAttempt = false,
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
        repo.languages.add(lang)
        val response = presenter.submit(submitRequest.copy(attempt = 1))
        assertEquals(
            response, SubmitResponse(
                output = "code",
                success = false,
                finalAttempt = false,
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
        repo.languages.add(lang)
        val response = presenter.submit(submitRequest.copy(attempt = 1))
        with(response) {
            assertEquals(true, success)
        }
        assertEquals(
            response, SubmitResponse(
                success = true,
                finalAttempt = false,
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
            unitTestRegex = "XCAssert"
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
            injectable = false,
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
            assignmentCodes = listOf(assignmentCode),
            title = "title",
        )
    }

}

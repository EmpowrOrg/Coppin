package org.empowrco.coppin.assignment.backend

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.sources.fakes.FakeAssignmentSource
import org.empowrco.coppin.sources.fakes.FakeLanguagesSource
import org.empowrco.coppin.utils.now
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class AssignmentRepositoryTest {
    private val assignmentSource = FakeAssignmentSource()
    private val languagesSource = FakeLanguagesSource()
    private val repo = RealAssignmentRepository(assignmentSource, languagesSource)

    @Test
    fun getAssignment() = runBlocking {
        val assignment = Assignment(
            id = UUID.randomUUID(),
            expectedOutput = "e_o",
            feedback = emptyList(),
            lastModifiedAt = LocalDateTime.Companion.now(),
            createdAt = LocalDateTime.Companion.now(),
            successMessage = "success",
            failureMessage = "failure",
            referenceId = "reference",
            totalAttempts = 4,
            instructions = "instructions",
            solution = "solution",
            assignmentCodes = emptyList(),
            title = "title",
        )
        assignmentSource.assignments.add(assignment)
        val result = repo.getAssignment("reference")
        assertEquals(assignment, result)
    }

    @Test
    fun createAssignment() = runBlocking {
        val assignment = Assignment(
            id = UUID.randomUUID(),
            expectedOutput = "e_o",
            feedback = emptyList(),
            lastModifiedAt = LocalDateTime.Companion.now(),
            createdAt = LocalDateTime.Companion.now(),
            successMessage = "success",
            failureMessage = "failure",
            referenceId = "reference",
            totalAttempts = 4,
            instructions = "instructions",
            solution = "solution",
            assignmentCodes = emptyList(),
            title = "title",
        )
        assignmentSource.assignments.add(assignment)
        repo.createAssignment(assignment)
    }

}

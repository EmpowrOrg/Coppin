package org.empowrco.coppin.assignment.backend

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.sources.fakes.FakeAssignmentSource
import org.empowrco.coppin.sources.fakes.FakeLanguagesSource
import org.empowrco.coppin.sources.fakes.FakeOpenAiSource
import org.empowrco.coppin.sources.fakes.FakeSettingsSource
import org.empowrco.coppin.sources.fakes.FakeSubmissionSource
import org.empowrco.coppin.utils.now
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class AssignmentApiRepositoryTest {
    private val assignmentSource = FakeAssignmentSource()
    private val languagesSource = FakeLanguagesSource()
    private val submissionSource = FakeSubmissionSource()
    private val settingsSource = FakeSettingsSource()
    private val openAiSource = FakeOpenAiSource()
    private val repo =
        RealAssignmentApiRepository(assignmentSource, languagesSource, submissionSource, settingsSource, openAiSource)

    @Test
    fun getAssignment() = runBlocking {
        val assignment = Assignment(
            id = UUID.randomUUID(),
            lastModifiedAt = LocalDateTime.Companion.now(),
            createdAt = LocalDateTime.Companion.now(),
            successMessage = "success",
            failureMessage = "failure",
            referenceId = "reference",
            totalAttempts = 4,
            instructions = "instructions",
            assignmentCodes = emptyList(),
            title = "title",
            archived = false,
            points = 2.0,
            blockId = null,
            courseId = UUID.randomUUID(),
            subject = Subject(
                id = UUID.randomUUID(),
                courseId = UUID.randomUUID(),
                name = "Functions",
                createdAt = LocalDateTime.Companion.now(),
                lastModifiedAt = LocalDateTime.Companion.now(),
            ),
        )
        assignmentSource.assignments.add(assignment)
        val result = repo.getAssignment("reference")
        assertEquals(assignment, result)
    }

}

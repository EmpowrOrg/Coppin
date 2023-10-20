package org.empowrco.coppin.sources.fakes

import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.sources.SubmissionSource
import java.util.UUID

class FakeSubmissionSource : SubmissionSource {

    val submissions = mutableListOf<Submission>()
    override suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission> {
        return submissions.filter { it.assignmentId == id && it.studentId == studentId }
    }

    override suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission> {
        return submissions.filter { it.assignmentId == id }.groupBy { it.studentId }.map { (key, value) ->
            value.maxBy { it.attempt }
        }
    }

    override suspend fun getLastStudentSubmissionForAssignment(id: UUID, studentId: String): Submission? {
        return submissions.filter { it.assignmentId == id && it.studentId == studentId }.maxByOrNull { it.attempt }
    }

    override suspend fun saveSubmission(submission: Submission) {
        submissions.add(submission)
    }

    override suspend fun getSubmission(id: UUID): Submission? {
        return submissions.firstOrNull { it.id == id }
    }
}

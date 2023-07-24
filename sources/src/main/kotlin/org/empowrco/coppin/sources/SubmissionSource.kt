package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Submissions
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface SubmissionSource {
    suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission>
    suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission>
    suspend fun getLastStudentSubmissionForAssignment(id: UUID, studentId: String): Submission?
    suspend fun saveSubmission(submission: Submission)
}

internal class RealSubmissionSource(cache: Cache) : SubmissionSource {

    private val cache = CacheSubmissionSource(cache)
    private val database = DatabaseSubmissionsSource()
    override suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission> {
        val submissions = cache.getSubmissionsForAssignment(id, studentId)
        return submissions.ifEmpty {
            database.getSubmissionsForAssignment(id, studentId).also {
                cache.saveSubmissionsForAssignment(id, studentId, it)
            }
        }
    }

    override suspend fun saveSubmission(submission: Submission) {
        database.saveSubmission(submission)
    }

    override suspend fun getLastStudentSubmissionForAssignment(id: UUID, studentId: String): Submission? {
        return database.getLastStudentSubmissionForAssignment(id, studentId)
    }

    override suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission> {
        return database.getLatestStudentSubmissionsForAssignment(id)
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheSubmissionSource(private val cache: Cache) : SubmissionSource {

    private fun submissionsKey(assignmentId: UUID, studentId: String) =
        "assignment:$assignmentId:$studentId:submissions"

    override suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission> {
        return cache.getList(submissionsKey(id, studentId), Submission::class.serializer())
            .sortedByDescending { it.attempt }
    }

    suspend fun saveSubmissionsForAssignment(id: UUID, studentId: String, submissions: List<Submission>) {
        cache.set(
            submissionsKey(id, studentId), json.encodeToString(
                ListSerializer(Submission::class.serializer()),
                submissions
            )
        )
    }

    override suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission> {
        throw NotImplementedError("Do not cache")
    }

    override suspend fun getLastStudentSubmissionForAssignment(id: UUID, studentId: String): Submission? {
        throw NotImplementedError("Do not cache")
    }

    override suspend fun saveSubmission(submission: Submission) {
        throw NotImplementedError("Do not cache the save")
    }
}

private class DatabaseSubmissionsSource : SubmissionSource {

    override suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission> = dbQuery {
        Submissions.select { (Submissions.assignment eq id) and (Submissions.studentId eq studentId) }
            .orderBy(Submissions.attempt, SortOrder.DESC).map { it.toSubmission() }
    }

    override suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission> = dbQuery {
        Submissions.select { (Submissions.assignment eq id) }.orderBy(Submissions.attempt, order = SortOrder.DESC)
            .distinctBy { Submissions.studentId }.map { it.toSubmission() }
    }

    override suspend fun getLastStudentSubmissionForAssignment(id: UUID, studentId: String): Submission? = dbQuery {
        Submissions.select { (Submissions.assignment eq id) and (Submissions.studentId eq studentId) }
            .orderBy(Submissions.attempt, SortOrder.DESC).limit(1).map { it.toSubmission() }.firstOrNull()
    }

    override suspend fun saveSubmission(submission: Submission) = dbQuery {
        Submissions.insert {
            it[Submissions.id] = submission.id
            it[Submissions.assignment] = submission.assignmentId
            it[Submissions.language] = submission.languageId
            it[Submissions.code] = submission.code
            it[Submissions.attempt] = submission.attempt
            it[Submissions.studentId] = submission.studentId
            it[Submissions.correct] = submission.correct
            it[Submissions.createdAt] = submission.createdAt
            it[Submissions.lastModifiedAt] = submission.lastModifiedAt
        }
        Unit
    }

    private fun ResultRow.toSubmission(): Submission {
        return Submission(
            id = this[Submissions.id].value,
            assignmentId = this[Submissions.assignment].value,
            languageId = this[Submissions.language].value,
            code = this[Submissions.code],
            attempt = this[Submissions.attempt],
            studentId = this[Submissions.studentId],
            correct = this[Submissions.correct],
            createdAt = this[Submissions.createdAt],
            lastModifiedAt = this[Submissions.lastModifiedAt],
        )
    }
}

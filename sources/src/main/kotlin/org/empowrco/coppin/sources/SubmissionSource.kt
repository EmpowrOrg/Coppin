package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Submissions
import org.empowrco.coppin.models.Submission
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface SubmissionSource {
    suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission>
    suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission>
    suspend fun getLastStudentSubmissionForAssignment(id: UUID, studentId: String): Submission?
    suspend fun saveSubmission(submission: Submission)
    suspend fun getSubmission(id: UUID): Submission?
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

    override suspend fun getSubmission(id: UUID): Submission? {
        return cache.getSubmission(id) ?: run {
            database.getSubmission(id)?.also {
                cache.saveSubmission(it)
            }
        }
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheSubmissionSource(private val cache: Cache) : SubmissionSource {

    private fun submissionsKey(assignmentId: UUID, studentId: String) =
        "submissions:$assignmentId:$studentId"

    private fun submissionKey(id: UUID) =
        "submissions:$id"

    override suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission> {
        return cache.getList(submissionsKey(id, studentId), Submission::class.serializer())
            .sortedByDescending { it.attempt }
    }

    suspend fun saveSubmissionsForAssignment(id: UUID, studentId: String, submissions: List<Submission>) {
        submissions.forEach {
            cache.delete(submissionKey(it.id))
            cache.delete(submissionsKey(id, studentId))
        }
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
        cache.set(submissionKey(submission.id), json.encodeToString(Submission::class.serializer(), submission))
        cache.delete(submissionsKey(submission.assignmentId, submission.studentId))
    }

    override suspend fun getSubmission(id: UUID): Submission? {
        return cache.get(submissionKey(id), Submission::class.serializer())
    }
}

private class DatabaseSubmissionsSource : SubmissionSource {

    override suspend fun getSubmissionsForAssignment(id: UUID, studentId: String): List<Submission> = dbQuery {
        Submissions.select { (Submissions.assignment eq id) and (Submissions.studentId eq studentId) }
            .orderBy(Submissions.attempt, SortOrder.DESC).map { it.toSubmission() }
    }

    override suspend fun getLatestStudentSubmissionsForAssignment(id: UUID): List<Submission> = dbQuery {
        val selectDistinctOn: CustomFunction<Boolean?> = distinctOn(
            Submissions.studentId
        )
        val selectColumns: List<Column<*>> = Submissions.columns
        val selectWhere: Op<Boolean> = (Submissions.assignment eq id)

        val query: Query = Submissions
            .slice(
                selectDistinctOn,
                *selectColumns.toTypedArray()
            )
            .select { selectWhere }
            .orderBy(Submissions.studentId).orderBy(Submissions.attempt, order = SortOrder.DESC)
        query.map { it.toSubmission() }
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
            it[Submissions.feedback] = submission.feedback
            it[Submissions.correct] = submission.correct
            it[Submissions.createdAt] = submission.createdAt
            it[Submissions.lastModifiedAt] = submission.lastModifiedAt
        }
        Unit
    }

    override suspend fun getSubmission(id: UUID): Submission? = dbQuery {
        Submissions.select { Submissions.id eq id }.limit(1).firstNotNullOfOrNull { it.toSubmission() }
    }

    private fun ResultRow.toSubmission(): Submission {
        return Submission(
            id = this[Submissions.id].value,
            assignmentId = this[Submissions.assignment].value,
            languageId = this[Submissions.language].value,
            code = this[Submissions.code],
            attempt = this[Submissions.attempt],
            studentId = this[Submissions.studentId],
            feedback = this[Submissions.feedback],
            correct = this[Submissions.correct],
            createdAt = this[Submissions.createdAt],
            lastModifiedAt = this[Submissions.lastModifiedAt],
        )
    }
}

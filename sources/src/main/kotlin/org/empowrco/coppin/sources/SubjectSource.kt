package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Subjects
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface SubjectSource {
    suspend fun getSubjectsForCourse(id: UUID): List<Subject>
    suspend fun getSubject(id: UUID): Subject?
    suspend fun createSubject(subject: Subject)
    suspend fun updateSubject(subject: Subject): Boolean
    suspend fun deleteSubject(subject: Subject): Boolean
}

internal class RealSubjectSource(cache: Cache, sectionsSource: SectionsSource) : SubjectSource {

    private val cache = CacheSubjectSource(cache)
    private val database = DatabaseSubjectSource(sectionsSource)

    override suspend fun getSubjectsForCourse(id: UUID): List<Subject> {
        return cache.getSubjectsForCourse(id).ifEmpty {
            database.getSubjectsForCourse(id).also {
                cache.saveSubjectsForCourse(id, it)
            }
        }
    }

    override suspend fun getSubject(id: UUID): Subject? {
        return cache.getSubject(id) ?: database.getSubject(id)?.also {
            cache.createSubject(it)
        }
    }

    override suspend fun createSubject(subject: Subject) {
        database.createSubject(subject)
        cache.createSubject(subject)
    }

    override suspend fun updateSubject(subject: Subject): Boolean {
        cache.updateSubject(subject)
        return database.updateSubject(subject)
    }

    override suspend fun deleteSubject(subject: Subject): Boolean {
        cache.deleteSubject(subject)
        return database.deleteSubject(subject)
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheSubjectSource(private val cache: Cache) : SubjectSource {

    private fun subjectsKey(id: UUID) = "subjects:$id"
    private fun subjectKey(id: UUID) = "subject:$id"

    override suspend fun getSubjectsForCourse(id: UUID): List<Subject> {
        return cache.getList(subjectsKey(id), Subject::class.serializer())
    }

    suspend fun saveSubjectsForCourse(id: UUID, subjects: List<Subject>) {
        cache.set(subjectsKey(id), json.encodeToString(subjects))
    }

    override suspend fun getSubject(id: UUID): Subject? {
        return cache.get(subjectKey(id), Subject::class.serializer())
    }

    override suspend fun createSubject(subject: Subject) {
        cache.set(subjectKey(subject.id), json.encodeToString(subject))
        cache.delete(subjectsKey(subject.courseId))
    }

    override suspend fun updateSubject(subject: Subject): Boolean {
        cache.delete(subjectKey(subject.id))
        cache.delete(subjectsKey(subject.courseId))
        return true
    }

    override suspend fun deleteSubject(subject: Subject): Boolean {
        cache.delete(subjectKey(subject.id))
        cache.delete(subjectsKey(subject.courseId))
        return true
    }
}

private class DatabaseSubjectSource(private val sectionsSource: SectionsSource): SubjectSource {
    override suspend fun getSubjectsForCourse(id: UUID): List<Subject> = dbQuery {
        Subjects.select { Subjects.course eq id }.orderBy(Subjects.name).map { it.toSubject(sectionsSource) }
    }

    override suspend fun getSubject(id: UUID): Subject? = dbQuery {
        Subjects.select { Subjects.id eq id }.limit(1).map { it.toSubject(sectionsSource) }.firstOrNull()
    }

    override suspend fun createSubject(subject: Subject) = dbQuery {
        Subjects.insert {
            it.build(subject)
        }
        Unit
    }

    override suspend fun updateSubject(subject: Subject): Boolean = dbQuery {
        Subjects.update({ Subjects.id eq subject.id }) {
            it.build(subject)
        } > 0
    }

    override suspend fun deleteSubject(subject: Subject) = dbQuery {
        Subjects.deleteWhere { Subjects.id eq subject.id } > 0
    }

    private fun UpdateBuilder<*>.build(subject: Subject) {
        this[Subjects.id] = subject.id
        this[Subjects.name] = subject.name
        this[Subjects.course] = subject.courseId
        this[Subjects.createdAt] = subject.createdAt
        this[Subjects.lastModifiedAt] = subject.lastModifiedAt
    }

    private suspend fun ResultRow.toSubject(sectionsSource: SectionsSource): Subject {
        val subjectId = this[Subjects.id].value
        val sections = sectionsSource.getSectionsForSubject(subjectId)
        return Subject(
            id = subjectId,
            name = this[Subjects.name],
            courseId = this[Subjects.course].value,
            sections = sections,
            createdAt = this[Subjects.createdAt],
            lastModifiedAt = this[Subjects.lastModifiedAt],
        )
    }
}

package org.empowrco.coppin.sources

import org.empowrco.coppin.db.Subjects
import org.empowrco.coppin.models.Subject
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
    suspend fun deleteSubject(id: UUID): Boolean
}

internal class RealSubjectSource : SubjectSource {
    override suspend fun getSubjectsForCourse(id: UUID): List<Subject> = dbQuery {
        Subjects.select { Subjects.course eq id }.orderBy(Subjects.name).map { it.toSubject() }
    }

    override suspend fun getSubject(id: UUID): Subject? = dbQuery {
        Subjects.select { Subjects.id eq id }.limit(1).map { it.toSubject() }.firstOrNull()
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

    override suspend fun deleteSubject(id: UUID) = dbQuery {
        Subjects.deleteWhere { Subjects.id eq id } > 0
    }

    private fun UpdateBuilder<*>.build(subject: Subject) {
        this[Subjects.id] = subject.id
        this[Subjects.name] = subject.name
        this[Subjects.course] = subject.courseId
        this[Subjects.createdAt] = subject.createdAt
        this[Subjects.lastModifiedAt] = subject.lastModifiedAt
    }

    private fun ResultRow.toSubject(): Subject {
        return Subject(
            id = this[Subjects.id].value,
            name = this[Subjects.name],
            courseId = this[Subjects.course].value,
            createdAt = this[Subjects.createdAt],
            lastModifiedAt = this[Subjects.lastModifiedAt],
        )
    }
}

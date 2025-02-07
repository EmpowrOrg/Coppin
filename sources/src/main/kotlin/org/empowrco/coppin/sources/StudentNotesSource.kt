package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.StudentNotes
import org.empowrco.coppin.models.StudentNote
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.UUID

interface StudentNotesSource {
    suspend fun getStudentNotes(noteId: UUID, studentId: String): List<StudentNote>
    suspend fun getStudentNote(id: UUID): StudentNote?
    suspend fun createStudentNote(studentNote: StudentNote)
    suspend fun updateStudentNote(studentNote: StudentNote): Boolean
    suspend fun deleteStudentNote(studentNote: StudentNote): Boolean
}

internal class RealStudentNotesSource(cache: Cache): StudentNotesSource {
    private val cache = CacheStudentNotesSource(cache)
    private val database = DatabaseStudentNotesSource()
    override suspend fun getStudentNotes(noteId: UUID, studentId: String): List<StudentNote> {
        return cache.getStudentNotes(noteId, studentId) ?: database.getStudentNotes(noteId, studentId).also {
            cache.createStudentNotes(noteId, studentId, it)
        }
    }

    override suspend fun getStudentNote(id: UUID): StudentNote? {
        return cache.getStudentNote(id) ?: database.getStudentNote(id)?.also {
            cache.createStudentNote(it)
        }
    }

    override suspend fun createStudentNote(studentNote: StudentNote) {
        database.createStudentNote(studentNote)
        cache.createStudentNote(studentNote)
    }

    override suspend fun updateStudentNote(studentNote: StudentNote): Boolean {
        if (database.updateStudentNote(studentNote)) {
            cache.updateStudentNote(studentNote)
            return true
        }
        return false
    }

    override suspend fun deleteStudentNote(studentNote: StudentNote): Boolean {
        cache.deleteStudentNote(studentNote)
        return database.deleteStudentNote(studentNote)
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheStudentNotesSource(private val cache: Cache): StudentNotesSource {

    private fun studentNotesKey(noteId: UUID, studentId: String) = "studentNotes:$noteId:$studentId"
    private fun studentNoteKey(id: UUID) = "studentNote:$id"

    override suspend fun getStudentNotes(noteId: UUID, studentId: String): List<StudentNote> {
        return cache.getList(studentNotesKey(noteId, studentId), StudentNote::class.serializer())
    }

    suspend fun createStudentNotes(noteId: UUID, studentId: String, notes: List<StudentNote>) {
        cache.set(studentNotesKey(noteId, studentId), json.encodeToString(notes))
    }

    override suspend fun getStudentNote(id: UUID): StudentNote? {
        return cache.get(studentNoteKey(id), StudentNote::class.serializer())
    }

    override suspend fun createStudentNote(studentNote: StudentNote) {
        deleteStudentNote(studentNote)
        cache.set(studentNoteKey(studentNote.id), json.encodeToString(studentNote))

    }

    override suspend fun updateStudentNote(studentNote: StudentNote): Boolean {
        createStudentNote(studentNote)
        return true
    }

    override suspend fun deleteStudentNote(studentNote: StudentNote): Boolean {
        cache.delete(studentNoteKey(studentNote.id))
        cache.delete(studentNotesKey(studentNote.noteId, studentNote.studentId))
        return true
    }
}

private class DatabaseStudentNotesSource: StudentNotesSource {
    override suspend fun getStudentNotes(noteId: UUID, studentId: String): List<StudentNote> {
        return StudentNotes.select { (StudentNotes.notes eq noteId) and (StudentNotes.studentId eq studentId) }
            .map { it.toStudentNote() }
    }

    override suspend fun getStudentNote(id: UUID): StudentNote {
        return StudentNotes.select { StudentNotes.id eq id }
            .map { it.toStudentNote() }
            .first()
    }

    override suspend fun createStudentNote(studentNote: StudentNote) {
        StudentNotes.insert {
            it.build(studentNote, false)
        }
    }

    override suspend fun updateStudentNote(studentNote: StudentNote): Boolean {
        return StudentNotes.update({ StudentNotes.id eq studentNote.id }) {
            it.build(studentNote, true)
        } > 0
    }

    override suspend fun deleteStudentNote(studentNote: StudentNote): Boolean {
        return StudentNotes.deleteWhere { StudentNotes.id eq  studentNote.id } > 0
    }

    private fun UpdateBuilder<*>.build(studentNote: StudentNote, isUpdate: Boolean) {
        this[StudentNotes.notes] = studentNote.noteId
        this[StudentNotes.comment] = studentNote.comment
        this[StudentNotes.contextAfter] = studentNote.contextAfter
        this[StudentNotes.contextBefore] = studentNote.contextBefore
        this[StudentNotes.highlightedText] = studentNote.highlightedText
        this[StudentNotes.index] = studentNote.index
        this[StudentNotes.contextType] = studentNote.contextType
        this[StudentNotes.type] = studentNote.type
        this[StudentNotes.studentId] = studentNote.studentId
        this[StudentNotes.lastModifiedAt] = studentNote.lastModifiedAt

        if (!isUpdate) {
            this[StudentNotes.id] = studentNote.id
            this[StudentNotes.createdAt] = studentNote.createdAt
        }
    }

    private fun ResultRow.toStudentNote(): StudentNote {
        return StudentNote(
            id = this[StudentNotes.id].value,
            noteId = this[StudentNotes.notes].value,
            comment = this[StudentNotes.comment],
            contextAfter = this[StudentNotes.contextAfter],
            contextBefore = this[StudentNotes.contextBefore],
            highlightedText = this[StudentNotes.highlightedText],
            index = this[StudentNotes.index],
            contextType = this[StudentNotes.contextType],
            type = this[StudentNotes.type],
            studentId = this[StudentNotes.studentId],
            createdAt = this[StudentNotes.createdAt],
            lastModifiedAt = this[StudentNotes.lastModifiedAt],
        )
    }
}
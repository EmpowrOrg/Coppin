package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Notes
import org.empowrco.coppin.models.Note
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.UUID

interface NotesSource {
    suspend fun getNote(id: UUID): Note?
    suspend fun updateNote(note: Note): Boolean
    suspend fun deleteNote(id: UUID): Boolean
    suspend fun createNote(note: Note)
    suspend fun getNotesForSection(sectionId: UUID): List<Note>
}

internal class RealNotesSource(cache: Cache): NotesSource {

    private val cache = CacheNotesSource(cache)
    private val database = DatabaseNotesSource()

    override suspend fun getNote(id: UUID): Note? {
        return cache.getNote(id) ?: database.getNote(id)?.also {
            cache.createNote(it)
        }
    }

    override suspend fun updateNote(note: Note): Boolean {
        if (database.updateNote(note)) {
            cache.updateNote(note)
            return true
        }
        return false
    }

    override suspend fun deleteNote(id: UUID): Boolean {
        cache.deleteNote(id)
        return database.deleteNote(id)
    }

    override suspend fun createNote(note: Note) {
        database.createNote(note)
        cache.createNote(note)
    }

    override suspend fun getNotesForSection(sectionId: UUID): List<Note> {
        TODO("Not yet implemented")
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheNotesSource(private val cache: Cache): NotesSource {
    private fun notesKey(id: UUID) = "notes:$id"

    override suspend fun getNote(id: UUID): Note? {
        return cache.get(notesKey(id), Note::class.serializer())
    }

    override suspend fun updateNote(note: Note): Boolean {
        cache.set(notesKey(note.id), json.encodeToString(note))
        return true
    }

    override suspend fun deleteNote(id: UUID): Boolean {
        cache.delete(notesKey(id))
        return true
    }

    override suspend fun createNote(note: Note) {
        cache.set(notesKey(note.id), json.encodeToString(note))
    }

    override suspend fun getNotesForSection(sectionId: UUID): List<Note> {
        throw NotImplementedError("Not yet implemented")
    }
}

private class DatabaseNotesSource: NotesSource {
    override suspend fun getNote(id: UUID): Note? = dbQuery {
        Notes.select { Notes.id eq id }.map { it.toNote() }.singleOrNull()
    }

    override suspend fun updateNote(note: Note): Boolean = dbQuery {
        Notes.update({Notes.id eq note.id}) {
            it.build(note, true)
        } > 0
    }

    override suspend fun deleteNote(id: UUID): Boolean = dbQuery {
        Notes.deleteWhere { Notes.id eq id } > 0
    }

    override suspend fun createNote(note: Note) = dbQuery {
        Notes.insert {
            it.build(note, false)
        }
        Unit
    }

    override suspend fun getNotesForSection(sectionId: UUID): List<Note> = dbQuery {
        TODO("Not yet implemented")
    }

    private fun ResultRow.toNote(): Note {
        return Note(
            id = this[Notes.id].value,
            notes = this[Notes.notes],
            brainrotUrl = this[Notes.brainrotUrl],
            podcastUrl = this[Notes.podcastUrl],
            summary = this[Notes.summary],
            createdAt = this[Notes.createdAt],
            lastModifiedAt = this[Notes.lastModifiedAt],
        )
    }

    private fun UpdateBuilder<*>.build(note: Note, isUpdate: Boolean) {
        this[Notes.notes] = note.notes
        this[Notes.brainrotUrl] = note.brainrotUrl
        this[Notes.podcastUrl] = note.podcastUrl
        this[Notes.summary] = note.summary
        this[Notes.lastModifiedAt] = note.lastModifiedAt
        if (!isUpdate) {
            this[Notes.id] = note.id
            this[Notes.createdAt] = note.createdAt
        }
    }
}
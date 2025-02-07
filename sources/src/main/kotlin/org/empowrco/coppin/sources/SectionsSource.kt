package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Sections
import org.empowrco.coppin.models.Section
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.UUID

interface SectionsSource {
    suspend fun getSectionsForSubject(id: UUID): List<Section>
    suspend fun createSection(section: Section)
    suspend fun updateSection(section: Section): Boolean
    suspend fun deleteSection(id: UUID): Boolean
    suspend fun getSection(id: UUID): Section?
}

internal class RealSectionsSource(
    cache: Cache
): SectionsSource {

    private val cache = CacheSectionsSource(cache)
    private val database = DatabaseSectionsSource()

    override suspend fun getSectionsForSubject(id: UUID): List<Section> {
        return cache.getSectionsForSubject(id).ifEmpty {
            database.getSectionsForSubject(id).also {
                cache.createSectionsForSubject(id, it)
            }
        }
    }

    override suspend fun createSection(section: Section) {
        database.createSection(section)
        cache.createSection(section)
    }

    override suspend fun updateSection(section: Section): Boolean {
        if (database.updateSection(section)) {
            cache.updateSection(section)
            return true
        }
        return false
    }

    override suspend fun deleteSection(id: UUID): Boolean {
        cache.deleteSection(id)
        return database.deleteSection(id)
    }

    override suspend fun getSection(id: UUID): Section? {
        return cache.getSection(id) ?: database.getSection(id)?.also {
            cache.createSection(it)
        }
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheSectionsSource(private val cache: Cache): SectionsSource {
    private fun sectionKey(id: UUID) = "section:$id"
    private fun sectionsKey(subjectId: UUID) = "sections:subject:$subjectId"
    override suspend fun getSectionsForSubject(id: UUID): List<Section> {
        return cache.getList(sectionsKey(id), Section::class.serializer())
    }

    suspend fun createSectionsForSubject(id: UUID, sections: List<Section>) {
        cache.set(sectionsKey(id), json.encodeToString(sections))
    }

    override suspend fun createSection(section: Section) {
        cache.set(sectionKey(section.id), json.encodeToString(section))
    }

    override suspend fun updateSection(section: Section): Boolean {
        cache.set(sectionKey(section.id), json.encodeToString(section))
        return true
    }

    override suspend fun deleteSection(id: UUID): Boolean {
        cache.delete(sectionKey(id))
        return true
    }

    override suspend fun getSection(id: UUID): Section? {
        return cache.get(sectionKey(id), Section::class.serializer())
    }
}

private class DatabaseSectionsSource: SectionsSource {
    override suspend fun getSectionsForSubject(id: UUID): List<Section> = dbQuery {
        Sections.select { Sections.subject eq id }.map { it.toSection() }
    }

    override suspend fun createSection(section: Section) = dbQuery {
        Sections.insert { it.build(section, false) }
        Unit
    }

    override suspend fun updateSection(section: Section): Boolean = dbQuery {
        Sections.update({ Sections.id eq section.id }) { it.build(section, true) } > 0
    }

    override suspend fun deleteSection(id: UUID): Boolean = dbQuery {
        Sections.deleteWhere { Sections.id eq  id } > 0
    }

    override suspend fun getSection(id: UUID): Section? = dbQuery {
        Sections.select { Sections.id eq id }.mapNotNull { it.toSection() }.singleOrNull()
    }

    private fun UpdateBuilder<*>.build(section: Section, isUpdate: Boolean) {
        if (!isUpdate) {
            this[Sections.id] = section.id
            this[Sections.createdAt] = section.createdAt
        }
        this[Sections.subject] = section.subjectId
        this[Sections.order] = section.order
        this[Sections.name] = section.name
        this[Sections.lastModifiedAt] = section.lastModifiedAt
    }

    private fun ResultRow.toSection(): Section {
        return Section(
            id = this[Sections.id].value,
            subjectId = this[Sections.subject].value,
            name = this[Sections.name],
            order = this[Sections.order],
            createdAt = this[Sections.createdAt],
            lastModifiedAt = this[Sections.lastModifiedAt]
        )
    }
}
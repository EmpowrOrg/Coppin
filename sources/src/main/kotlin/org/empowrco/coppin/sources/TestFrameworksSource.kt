package org.empowrco.coppin.sources

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.empowrco.coppin.db.Frameworks
import org.empowrco.coppin.db.FrameworksCommands
import org.empowrco.coppin.models.Language.Framework
import org.empowrco.coppin.models.Language.Framework.Command
import java.util.UUID
import org.empowrco.coppin.utils.serialization.json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

interface FrameworksSource {
    suspend fun getFrameworksForLanguage(languageId: UUID): List<Framework>
    suspend fun createFramework(framework: Framework)
    suspend fun updateFramework(framework: Framework): Boolean
    suspend fun deleteFramework(framework: Framework): Boolean
    suspend fun getFramework(frameworkId: UUID): Framework?
}

internal class RealFrameworksSource(cache: Cache) : FrameworksSource {
    private val cache = CacheFrameworksSource(cache)
    private val database = DatabaseFrameworksSource()

    override suspend fun getFrameworksForLanguage(languageId: UUID): List<Framework> {
        return cache.getFrameworksForLanguage(languageId).ifEmpty {
            database.getFrameworksForLanguage(languageId).also {
                cache.createFrameworksForLanguage(it)
            }
        }
    }

    override suspend fun createFramework(framework: Framework) {
        database.createFramework(framework)
        cache.createFramework(framework)
    }

    override suspend fun updateFramework(framework: Framework): Boolean {
        cache.updateFramework(framework)
        return database.updateFramework(framework)
    }

    override suspend fun deleteFramework(framework: Framework): Boolean {
        cache.deleteFramework(framework)
        return database.deleteFramework(framework)
    }

    override suspend fun getFramework(frameworkId: UUID): Framework? {
        return cache.getFramework(frameworkId) ?: database.getFramework(frameworkId)?.also {
            cache.createFramework(it)
        }
    }
}

@OptIn(InternalSerializationApi::class)
private class CacheFrameworksSource(private val cache: Cache) : FrameworksSource {
    private fun frameworksKey(id: UUID) = "frameworks:$id"
    private fun frameworksLanguageKey(id: UUID) = "frameworks:language:$id"

    override suspend fun getFrameworksForLanguage(languageId: UUID): List<Framework> {
        return cache.getList(frameworksLanguageKey(languageId), Framework::class.serializer()) ?: emptyList()
    }

    suspend fun createFrameworksForLanguage(frameworks: List<Framework>) {
        val languageId = frameworks.firstOrNull()?.languageId ?: return
        cache.set(frameworksLanguageKey(languageId), json.encodeToString(frameworks))
    }

    override suspend fun createFramework(framework: Framework) {
        cache.set(frameworksKey(framework.id), json.encodeToString(framework))
    }

    override suspend fun updateFramework(framework: Framework): Boolean {
        deleteFramework(framework)
        deleteFrameworksForLanguage(framework.languageId)
        return true
    }

    override suspend fun deleteFramework(framework: Framework): Boolean {
        cache.delete(frameworksKey(framework.id))
        deleteFrameworksForLanguage(framework.languageId)
        return true
    }

    suspend fun deleteFrameworksForLanguage(languageId: UUID) {
        cache.delete(frameworksLanguageKey(languageId))
    }

    override suspend fun getFramework(frameworkId: UUID): Framework? {
        return cache.get(frameworksKey(frameworkId), Framework::class.serializer())
    }
}

private class DatabaseFrameworksSource : FrameworksSource {
    override suspend fun getFrameworksForLanguage(languageId: UUID): List<Framework> = dbQuery {
        Frameworks.select { Frameworks.language eq languageId }.map { it.toFramework() }
    }

    override suspend fun createFramework(framework: Framework) = dbQuery {
        Frameworks.insert {
            it.build(framework, isUpdate = false)
        }
        FrameworksCommands.batchInsert(framework.commands) { command ->
            this.build(command, isUpdate = false)
        }
        Unit
    }

    override suspend fun updateFramework(framework: Framework): Boolean {
        val result = dbQuery {
            val result = Frameworks.update({ Frameworks.id eq framework.id }) {
                it.build(framework, isUpdate = true)
            } > 0
            FrameworksCommands.deleteWhere { FrameworksCommands.framework eq framework.id }
            result
        }
        if (result) {
            dbQuery {
                FrameworksCommands.batchInsert(framework.commands) { command ->
                    this.build(command, isUpdate = false)
                }
            }
        }
        return result
    }

    override suspend fun deleteFramework(framework: Framework): Boolean = dbQuery {
        Frameworks.deleteWhere { Frameworks.id eq framework.id } > 0
    }

    override suspend fun getFramework(frameworkId: UUID): Framework? = dbQuery {
        Frameworks.select { Frameworks.id eq frameworkId }.map { it.toFramework() }.firstOrNull()
    }

    private fun ResultRow.toFramework(): Framework {
        val frameworkId = this[Frameworks.id].value
        val commands =
            FrameworksCommands.select { FrameworksCommands.framework eq frameworkId }.orderBy(FrameworksCommands.order)
                .map { it.toCommand() }
        return Framework(
            id = frameworkId,
            languageId = this[Frameworks.language].value,
            name = this[Frameworks.name],
            version = this[Frameworks.version],
            commands = commands,
            createdAt = this[Frameworks.createdAt],
            lastModifiedAt = this[Frameworks.lastModifiedAt],
        )
    }

    private fun ResultRow.toCommand(): Command {
        return Command(
            id = this[FrameworksCommands.id].value,
            command = this[FrameworksCommands.command],
            order = this[FrameworksCommands.order],
            createdAt = this[FrameworksCommands.createdAt],
            lastModifiedAt = this[FrameworksCommands.lastModifiedAt],
        )
    }

    private fun UpdateBuilder<*>.build(framework: Framework, isUpdate: Boolean) {
        this[Frameworks.name] = framework.name
        this[Frameworks.language] = framework.languageId
        this[Frameworks.version] = framework.version
        if (!isUpdate) {
            this[Frameworks.id] = framework.id
            this[Frameworks.createdAt] = framework.createdAt
        }
        this[Frameworks.lastModifiedAt] = framework.lastModifiedAt
    }

    private fun UpdateBuilder<*>.build(command: Command, isUpdate: Boolean) {
        this[FrameworksCommands.command] = command.command
        this[FrameworksCommands.order] = command.order
        if (!isUpdate) {
            this[FrameworksCommands.id] = command.id
            this[FrameworksCommands.createdAt] = command.createdAt
        }
        this[FrameworksCommands.lastModifiedAt] = command.lastModifiedAt
    }
}


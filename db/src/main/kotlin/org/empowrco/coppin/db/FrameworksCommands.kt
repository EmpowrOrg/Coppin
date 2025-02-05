package org.empowrco.coppin.db

import org.jetbrains.exposed.sql.ReferenceOption

object FrameworksCommands: BaseTable() {
    val framework = reference("framework", Frameworks.id, onDelete = ReferenceOption.CASCADE)
    val command = text("command")
    val order = integer("order")

    init {
        uniqueIndex(framework, order)
    }
}
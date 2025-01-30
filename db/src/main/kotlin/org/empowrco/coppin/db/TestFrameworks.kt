package org.empowrco.coppin.db

import org.jetbrains.exposed.sql.ReferenceOption

object TestFrameworks: BaseTable() {
    val name = varchar("name", ColumnConfig.name)
    val language = reference("language", Languages.id, onDelete = ReferenceOption.CASCADE)
}
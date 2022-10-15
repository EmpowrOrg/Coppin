package org.empowrco.coppin.db

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

open class BaseTable : UUIDTable() {
    val createdAt = datetime("created_at")
    val lastModifiedAt = datetime("last_modified_at")
}

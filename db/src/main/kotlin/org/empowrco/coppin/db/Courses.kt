package org.empowrco.coppin.db

import org.jetbrains.exposed.dao.id.UUIDTable

object Courses : UUIDTable() {
    val edxId = text("edx_id")
    val title = text("title")
    val number = text("number")
    val org = text("org")
}

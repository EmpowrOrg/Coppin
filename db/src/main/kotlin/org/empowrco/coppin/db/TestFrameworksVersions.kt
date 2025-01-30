package org.empowrco.coppin.db

import org.jetbrains.exposed.sql.ReferenceOption

object TestFrameworksVersions: BaseTable() {
    val testFramework = reference("test_framework", TestFrameworks.id, onDelete = ReferenceOption.CASCADE)
    val version = integer("version")
    val instruction = text("instruction")
}
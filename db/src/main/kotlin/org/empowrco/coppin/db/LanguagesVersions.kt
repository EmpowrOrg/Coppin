package org.empowrco.coppin.db

object LanguagesVersions: BaseTable() {
    val version = integer("version")
    val language = reference("language", Languages.id)
}
package org.empowrco.coppin.db

object LanguagesVersions: BaseTable() {
    val version = text("version")
    val language = reference("language", Languages.id)
}
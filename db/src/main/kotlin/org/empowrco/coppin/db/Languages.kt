package org.empowrco.coppin.db

object Languages: BaseTable() {
    val mime = varchar("mime", 50).uniqueIndex()
    val name = varchar("name", 50)
    val url = text("url")
}

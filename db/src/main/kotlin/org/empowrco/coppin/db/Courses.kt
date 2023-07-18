package org.empowrco.coppin.db

object Courses : BaseTable() {
    val edxId = text("edx_id").uniqueIndex()
    val title = text("title")
    val number = text("number")
    val org = text("org")
}

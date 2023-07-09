package org.empowrco.coppin.db

object Courses : BaseTable() {
    val edxId = text("edx_id")
    val title = text("title")
    val number = text("number")
    val org = text("org")
}

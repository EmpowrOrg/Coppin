package org.empowrco.coppin.db

object Sections: BaseTable() {
    val name = varchar("name", ColumnConfig.name)
    val subject = reference("subject", Subjects.id)
    val order = integer("order")
}
package org.empowrco.coppin.db

object Applications: BaseTable() {
    val name = varchar("name", ColumnConfig.name)
}
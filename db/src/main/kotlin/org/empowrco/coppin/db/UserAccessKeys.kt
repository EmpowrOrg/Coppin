package org.empowrco.coppin.db

object UserAccessKeys : BaseTable() {
    val user = reference("user", Users.id)
    val key = text("key_hash")
    val name = varchar("name", ColumnConfig.name)

    init {
        uniqueIndex(user, key)
        uniqueIndex(user, name)
    }
}

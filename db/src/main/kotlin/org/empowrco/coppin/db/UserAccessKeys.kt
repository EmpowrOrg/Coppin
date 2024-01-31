package org.empowrco.coppin.db

import org.empowrco.coppin.models.UserAccessKey

object UserAccessKeys : BaseTable() {
    val user = reference("user", Users.id)
    val key = text("key_hash")
    val name = varchar("name", ColumnConfig.name)
    val type = enumeration<UserAccessKey.Type>("type")

    init {
        uniqueIndex(user, key)
        uniqueIndex(user, name)
    }
}

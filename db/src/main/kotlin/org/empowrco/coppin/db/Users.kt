package org.empowrco.coppin.db

import org.empowrco.coppin.models.User

object Users : BaseTable() {
    val firstName = varchar("first_name", ColumnConfig.name)
    val lastName = varchar("last_name", ColumnConfig.name)
    val email = varchar("email", ColumnConfig.username).uniqueIndex()
    val passwordHash = varchar("password_hash", ColumnConfig.password)
    val type = enumeration<User.Type>("type")
    val isAuthorized = bool("is_authorized")
}

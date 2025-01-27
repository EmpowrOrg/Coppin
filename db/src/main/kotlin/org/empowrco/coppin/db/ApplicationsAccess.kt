package org.empowrco.coppin.db

import org.empowrco.coppin.models.User
import org.jetbrains.exposed.sql.ReferenceOption

object ApplicationsAccess: BaseTable() {
    val application = reference("application", Applications.id, onDelete = ReferenceOption.CASCADE)
    val type = enumeration<User.Type>("type")
    
    init {
        uniqueIndex(application, type)
    }
}
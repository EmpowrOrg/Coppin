package org.empowrco.coppin.db

object UserAccessKeys : BaseTable() {
    val user = reference("user", Users.id)
    val key = text("key_hash")

    init {
        uniqueIndex(user, key)
    }
}

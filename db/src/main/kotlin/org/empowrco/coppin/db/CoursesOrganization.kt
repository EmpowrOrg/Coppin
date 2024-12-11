package org.empowrco.coppin.db

object CoursesOrganization : BaseTable() {
    val name = text("name")
    val address = reference("address_id", Addresses.id).nullable()
    val phone = text("phone").nullable()
    val email = text("email")
    val website = text("website").nullable()
}
package org.empowrco.coppin.db

object Addresses : BaseTable() {
    val line1 = text("line1")
    val line2 = text("line2").nullable()
    val city = text("city")
    val state = text("state")
    val zip = text("zip")
    val country = text("country")
}
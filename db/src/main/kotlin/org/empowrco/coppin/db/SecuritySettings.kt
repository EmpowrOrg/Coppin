package org.empowrco.coppin.db

object SecuritySettings : BaseTable() {
    val okta = bool("okta_enabled")
}

package org.empowrco.coppin.db

object OrgSettings : BaseTable() {
    val edxClientId = text("edx_client_id")
    val edxClientSecret = text("edx_client_secret")
    val edxUsername = text("edx_username")
    val edxApiUrl = text("edx_api_url")
    val doctorUrl = text("doctor_url")
}

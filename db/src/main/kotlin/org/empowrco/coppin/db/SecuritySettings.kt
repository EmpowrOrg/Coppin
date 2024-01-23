package org.empowrco.coppin.db

object SecuritySettings : BaseTable() {
    val oktaEnabled = bool("okta_enabled")
    val oktaClientSecret = text("okta_client_secret")
    val oktaClientSecretDisplay = text("okta_client_secret_display")
    val oktaClientId = text("okta_client_id")
    val oktaDomain = text("okta_domain")
}

package org.empowrco.coppin.admin.presenters

import kotlinx.serialization.Serializable

@Serializable
data class GetSecuritySettingsResponse(
    val oktaEnabled: Boolean,
    val clientId: String,
    val clientSecret: String,
    val oktaDomain: String,
    val userId: String,
)

@Serializable
object SaveSecuritySettingsResponse

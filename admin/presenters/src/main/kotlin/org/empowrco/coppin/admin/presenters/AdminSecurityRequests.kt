package org.empowrco.coppin.admin.presenters

import kotlinx.serialization.Serializable

@Serializable
data class GetSecuritySettingsRequest(val email: String?)

@Serializable
data class SaveSecuritySettingsRequest(
    val userId: String,
    val enableOkta: Boolean,
    val clientId: String?,
    val clientSecret: String?,
    val oktaDomain: String?,
)

@Serializable
data class GetOrgSettingsRequest(val email: String?)

@Serializable
data class SaveOrgSettingsRequest(
    val userEmail: String,
    val edxClientId: String?,
    val edxClientSecret: String?,
    val edxUsername: String?,
    val edxApiUrl: String?,
    val doctorUrl: String?,
)


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

@Serializable
data class GetOrgSettingsResponse(
    val edxClientId: String,
    val edxClientSecretDisplay: String,
    val edxUsername: String,
    val edxApiUrl: String,
    val doctorUrl: String,
)

@Serializable
object SaveOrgSettingsResponse

@Serializable
data class GetAiSettingsResponse(
    val org: String,
    val model: String,
    val key: String,
    val prePrompt: String,
)

@Serializable
object SaveAiSettingsResponse

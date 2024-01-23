package org.empowrco.coppin.admin.presenters

import kotlinx.serialization.Serializable

@Serializable
data class GetSecuritySettingsRequest(val userId: String?)

@Serializable
data class SaveSecuritySettingsRequest(
    val password: String,
    val userId: String,
    val enableOkta: Boolean,
    val clientId: String?,
    val clientSecret: String?,
    val oktaDomain: String?,
)


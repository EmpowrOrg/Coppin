package org.empowrco.coppin.models.responses

data class AiResponse(
    val response: String?,
    val stopReason: String?,
    val promptTokens: Int,
    val responseTokens: Int,
)

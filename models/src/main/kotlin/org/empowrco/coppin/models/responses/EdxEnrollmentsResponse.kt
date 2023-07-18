package org.empowrco.coppin.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class EdxEnrollmentsResponse(val results: List<EdxStudent>)

package org.empowrco.coppin.assignment.backend

import kotlinx.serialization.Serializable

@Serializable
data class AssignmentCodeResponse(val success: Boolean, val output: String)
